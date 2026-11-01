const conversationId = "sessao-web-" + Math.random().toString(36).substring(2, 9);

const micBtn = document.getElementById('mic-btn');
const micLabel = document.getElementById('mic-label');
const chatForm = document.getElementById('chat-form');
const textInput = document.getElementById('text-input');
const chatMessages = document.getElementById('chat-messages');

// Charts Instances
let categoryChartInstance = null;
let flowChartInstance = null;

// Palette for Category Donut
const categoryPalette = [
    '#6366f1', '#10b981', '#f59e0b', '#ef4444', 
    '#ec4899', '#8b5cf6', '#06b6d4', '#14b8a6', 
    '#f97316', '#a855f7', '#64748b'
];

// MediaRecorder for Mic
let mediaRecorder = null;
let audioChunks = [];
let isRecording = false;

micBtn.addEventListener('click', async () => {
    if (isRecording) {
        stopRecording();
    } else {
        startRecording();
    }
});

async function startRecording() {
    try {
        const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
        mediaRecorder = new MediaRecorder(stream);
        audioChunks = [];

        mediaRecorder.ondataavailable = (event) => {
            if (event.data.size > 0) {
                audioChunks.push(event.data);
            }
        };

        mediaRecorder.onstop = async () => {
            stream.getTracks().forEach(track => track.stop());
            if (audioChunks.length > 0) {
                const audioBlob = new Blob(audioChunks, { type: 'audio/webm' });
                sendVoiceAudio(audioBlob);
            }
        };

        mediaRecorder.start();
        isRecording = true;
        micBtn.classList.add('recording');
        micLabel.classList.add('recording');
        micLabel.textContent = "🔴 Gravando... Fale e clique no microfone para enviar";

    } catch (err) {
        console.error("Erro ao acessar microfone:", err);
        micLabel.textContent = "⚠️ Permissão do microfone negada. Permita o microfone no navegador.";
    }
}

function stopRecording() {
    if (mediaRecorder && isRecording) {
        mediaRecorder.stop();
        isRecording = false;
        micBtn.classList.remove('recording');
        micLabel.classList.remove('recording');
        micLabel.textContent = "Processando áudio com a IA...";
    }
}

async function sendVoiceAudio(blob) {
    const loadingId = appendMessage('assistant', 'Processando seu comando de voz...');
    scrollToBottom();

    try {
        const formData = new FormData();
        formData.append('file', blob, 'audio.webm');
        formData.append('conversationId', conversationId);

        const response = await fetch('/api/v1/assistant/voice', {
            method: 'POST',
            body: formData
        });

        if (!response.ok) throw new Error("Erro na transcrição/processamento");
        const data = await response.json();

        if (data.transcription) {
            appendMessage('user', data.transcription);
        }

        updateMessage(loadingId, data.response);
        scrollToBottom();

        micLabel.textContent = "Clique no microfone e fale seu comando";
        refreshData();

    } catch (err) {
        updateMessage(loadingId, "Desculpe, ocorreu um erro ao processar o áudio. Tente novamente ou use o campo de texto.");
        micLabel.textContent = "Clique no microfone e fale seu comando";
        console.error(err);
    }
}

chatForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const message = textInput.value.trim();
    if (message) {
        sendMessage(message);
        textInput.value = '';
    }
});

async function sendMessage(text) {
    appendMessage('user', text);
    scrollToBottom();

    const loadingId = appendMessage('assistant', 'Processando...');
    scrollToBottom();

    try {
        const response = await fetch('/api/v1/assistant/chat', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ message: text, conversationId: conversationId })
        });

        if (!response.ok) throw new Error("Erro na resposta da API");
        const data = await response.json();

        updateMessage(loadingId, data.response);
        scrollToBottom();

        refreshData();

    } catch (err) {
        updateMessage(loadingId, "Desculpe, ocorreu um erro ao se comunicar com a IA. Verifique se o servidor está rodando.");
        console.error(err);
    }
}

function appendMessage(role, text) {
    const id = 'msg-' + Date.now() + Math.random().toString(36).substring(2, 5);
    const div = document.createElement('div');
    div.className = `message ${role}`;
    div.id = id;
    div.innerHTML = text.replace(/\n/g, '<br>');
    chatMessages.appendChild(div);
    return id;
}

function updateMessage(id, text) {
    const elem = document.getElementById(id);
    if (elem) elem.innerHTML = text.replace(/\n/g, '<br>');
}

function scrollToBottom() {
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

// Fetch Metrics, Transactions & Update Charts
async function refreshData() {
    try {
        const [summaryRes, txRes] = await Promise.all([
            fetch('/api/v1/transactions/summary'),
            fetch('/api/v1/transactions')
        ]);

        if (summaryRes.ok) {
            const s = await summaryRes.json();
            const balElem = document.getElementById('metric-balance');
            balElem.textContent = formatCurrency(s.netBalance || 0);
            balElem.className = 'metric-value ' + ((s.netBalance >= 0) ? 'balance-pos' : 'balance-neg');

            document.getElementById('metric-income').textContent = formatCurrency(s.totalIncome || 0);
            document.getElementById('metric-expense').textContent = formatCurrency(s.totalExpense || 0);
            document.getElementById('metric-count').textContent = s.totalTransactions || 0;

            // Update Charts
            updateCharts(s);
        }

        if (txRes.ok) {
            const list = await txRes.json();
            renderTransactions(list);
        }
    } catch (e) {
        console.error("Erro ao atualizar métricas:", e);
    }
}

function updateCharts(summary) {
    const expensesByCategory = summary.expensesByCategory || {};
    const categories = Object.keys(expensesByCategory);
    const amounts = Object.values(expensesByCategory);

    document.getElementById('category-chart-subtitle').textContent = 
        `${categories.length} categoria${categories.length === 1 ? '' : 's'}`;

    // 1. Donut Chart: Expenses by Category
    const ctxCat = document.getElementById('categoryChart').getContext('2d');
    
    const catLabels = categories.length > 0 ? categories : ['Sem despesas'];
    const catData = amounts.length > 0 ? amounts : [1];
    const catColors = amounts.length > 0 
        ? categoryPalette.slice(0, categories.length)
        : ['#1e293b'];

    if (categoryChartInstance) {
        categoryChartInstance.data.labels = catLabels;
        categoryChartInstance.data.datasets[0].data = catData;
        categoryChartInstance.data.datasets[0].backgroundColor = catColors;
        categoryChartInstance.update();
    } else {
        categoryChartInstance = new Chart(ctxCat, {
            type: 'doughnut',
            data: {
                labels: catLabels,
                datasets: [{
                    data: catData,
                    backgroundColor: catColors,
                    borderWidth: 2,
                    borderColor: '#111827'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutout: '70%',
                plugins: {
                    legend: {
                        position: 'right',
                        labels: {
                            color: '#cbd5e1',
                            font: { family: 'Plus Jakarta Sans', size: 12 },
                            boxWidth: 14,
                            padding: 12
                        }
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                if (categories.length === 0) return ' Nenhuma despesa registrada';
                                const val = context.raw || 0;
                                return ` ${context.label}: ${formatCurrency(val)}`;
                            }
                        }
                    }
                }
            }
        });
    }

    // 2. Bar Chart: Cash Flow (Income vs Expense)
    const ctxFlow = document.getElementById('flowChart').getContext('2d');
    const totalIncome = summary.totalIncome || 0;
    const totalExpense = summary.totalExpense || 0;

    if (flowChartInstance) {
        flowChartInstance.data.datasets[0].data = [totalIncome, totalExpense];
        flowChartInstance.update();
    } else {
        flowChartInstance = new Chart(ctxFlow, {
            type: 'bar',
            data: {
                labels: ['Total Receitas', 'Total Despesas'],
                datasets: [{
                    data: [totalIncome, totalExpense],
                    backgroundColor: [
                        'rgba(16, 185, 129, 0.85)',
                        'rgba(239, 68, 68, 0.85)'
                    ],
                    borderColor: [
                        '#10b981',
                        '#ef4444'
                    ],
                    borderWidth: 1.5,
                    borderRadius: 8,
                    maxBarThickness: 60
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return ` ${formatCurrency(context.raw)}`;
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        grid: { display: false },
                        ticks: {
                            color: '#cbd5e1',
                            font: { family: 'Plus Jakarta Sans', size: 12, weight: '600' }
                        }
                    },
                    y: {
                        grid: { color: 'rgba(255, 255, 255, 0.05)' },
                        ticks: {
                            color: '#9ca3af',
                            font: { family: 'Plus Jakarta Sans', size: 11 },
                            callback: function(val) {
                                return 'R$ ' + val;
                            }
                        }
                    }
                }
            }
        });
    }
}

function renderTransactions(list) {
    const container = document.getElementById('feed-list');
    if (!list || list.length === 0) {
        container.innerHTML = '<div class="empty-state">Nenhuma transação registrada ainda.<br>Use a voz ou o chat para adicionar!</div>';
        return;
    }

    container.innerHTML = list.map(tx => {
        const isIncome = tx.type === 'INCOME';
        const sign = isIncome ? '+' : '-';
        const amountClass = isIncome ? 'income' : 'expense';

        return `
            <div class="tx-item">
                <div class="tx-info">
                    <span class="tx-desc">${escapeHtml(tx.description)}</span>
                    <div class="tx-meta">
                        <span class="badge-cat">${escapeHtml(tx.categoryDisplayName || tx.category)}</span>
                        <span>${formatDate(tx.createdAt)}</span>
                    </div>
                </div>
                <div style="display:flex; align-items:center;">
                    <span class="tx-amount ${amountClass}">${sign} ${formatCurrency(tx.amount)}</span>
                    <button class="tx-delete-btn" onclick="deleteTx('${tx.id}')" title="Estornar transação">🗑️</button>
                </div>
            </div>
        `;
    }).join('');
}

async function deleteTx(id) {
    if (!confirm("Deseja estornar/remover esta transação?")) return;
    try {
        const res = await fetch(`/api/v1/transactions/${id}`, { method: 'DELETE' });
        if (res.ok) {
            appendMessage('assistant', `Transação estornada com sucesso.`);
            refreshData();
        }
    } catch (err) {
        alert("Erro ao remover transação");
    }
}

function formatCurrency(val) {
    return Number(val).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

function formatDate(dateStr) {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    return d.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
}

function escapeHtml(str) {
    return (str || '').replace(/[&<>'"]/g, tag => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;'
    }[tag]));
}

// Init load
refreshData();
