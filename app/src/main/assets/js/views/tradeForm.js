/**
 * DayForge - Trade Form Module
 * Handles paper trade logging
 */

const TradeForm = {
    modal: null,
    currentDate: null,
    editingTradeId: null,

    /**
     * Initialize trade form
     */
    init() {
        this.modal = document.getElementById('tradeModal');
        this.bindEvents();
    },

    /**
     * Bind event listeners
     */
    bindEvents() {
        document.getElementById('tradeModalClose')?.addEventListener('click', () => this.close());
        document.getElementById('tradeModalCancel')?.addEventListener('click', () => this.close());
        document.getElementById('tradeModalSave')?.addEventListener('click', () => this.saveTrade());

        this.modal?.addEventListener('click', (e) => {
            if (e.target === this.modal) {
                this.close();
            }
        });
    },

    /**
     * Open trade modal
     */
    open(date = new Date()) {
        this.currentDate = date;
        this.editingTradeId = null;
        this.clearForm();
        this.renderTrades();
        this.modal.classList.add('active');
    },

    /**
     * Clear the form
     */
    clearForm() {
        document.getElementById('tradeAsset').value = '';
        document.getElementById('tradeBias').value = '';
        document.getElementById('tradeEntry').value = '';
        document.getElementById('tradeStopLoss').value = '';
        document.getElementById('tradeTakeProfit').value = '';
        document.getElementById('tradeResult').value = 'pending';
        document.getElementById('tradeNotes').value = '';
    },

    /**
     * Render trades list
     */
    renderTrades() {
        const container = document.getElementById('tradesContainer');
        if (!container) return;

        const trades = Storage.getTrades(this.currentDate);

        if (trades.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <p>No trades logged today. Add your first trade above.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = trades.map(trade => `
            <div class="trade-item" data-trade-id="${trade.id}">
                <div class="trade-item-info">
                    <span class="trade-item-asset">${trade.asset}</span>
                    <span class="trade-item-details">
                        Entry: ${trade.entry} | SL: ${trade.stopLoss} | TP: ${trade.takeProfit}
                    </span>
                </div>
                <span class="trade-item-bias ${trade.bias}">${trade.bias.toUpperCase()}</span>
                <span class="trade-item-result ${trade.result}">${trade.result.toUpperCase()}</span>
                <div class="trade-item-actions">
                    <button class="btn btn-sm edit-trade" data-trade-id="${trade.id}">Edit</button>
                    <button class="btn btn-sm btn-danger delete-trade" data-trade-id="${trade.id}">×</button>
                </div>
            </div>
        `).join('');

        // Bind action buttons
        container.querySelectorAll('.edit-trade').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.stopPropagation();
                this.editTrade(parseInt(btn.dataset.tradeId));
            });
        });

        container.querySelectorAll('.delete-trade').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.stopPropagation();
                this.deleteTrade(parseInt(btn.dataset.tradeId));
            });
        });
    },

    /**
     * Save or update a trade
     */
    saveTrade() {
        const asset = document.getElementById('tradeAsset').value.trim();
        const bias = document.getElementById('tradeBias').value;
        const entry = document.getElementById('tradeEntry').value;
        const stopLoss = document.getElementById('tradeStopLoss').value;
        const takeProfit = document.getElementById('tradeTakeProfit').value;
        const result = document.getElementById('tradeResult').value;
        const notes = document.getElementById('tradeNotes').value.trim();

        // Validate required fields
        if (!asset || !bias || !entry || !stopLoss || !takeProfit) {
            App.showToast('Please fill in all required fields', 'warning');
            return;
        }

        const trade = {
            asset,
            bias,
            entry: parseFloat(entry),
            stopLoss: parseFloat(stopLoss),
            takeProfit: parseFloat(takeProfit),
            result,
            notes
        };

        if (this.editingTradeId) {
            // Update existing trade
            Storage.updateTrade(this.editingTradeId, trade, this.currentDate);
            App.showToast('Trade updated', 'success');
            this.editingTradeId = null;
        } else {
            // Add new trade
            Storage.saveTrade(trade, this.currentDate);
            App.showToast('Trade added', 'success');
        }

        this.clearForm();
        this.renderTrades();
        document.getElementById('tradeModalSave').textContent = 'Add Trade';
    },

    /**
     * Edit a trade
     */
    editTrade(tradeId) {
        const trades = Storage.getTrades(this.currentDate);
        const trade = trades.find(t => t.id === tradeId);

        if (!trade) return;

        this.editingTradeId = tradeId;

        document.getElementById('tradeAsset').value = trade.asset;
        document.getElementById('tradeBias').value = trade.bias;
        document.getElementById('tradeEntry').value = trade.entry;
        document.getElementById('tradeStopLoss').value = trade.stopLoss;
        document.getElementById('tradeTakeProfit').value = trade.takeProfit;
        document.getElementById('tradeResult').value = trade.result;
        document.getElementById('tradeNotes').value = trade.notes || '';

        document.getElementById('tradeModalSave').textContent = 'Update Trade';
    },

    /**
     * Delete a trade
     */
    deleteTrade(tradeId) {
        if (confirm('Are you sure you want to delete this trade?')) {
            Storage.deleteTrade(tradeId, this.currentDate);
            this.renderTrades();
            App.showToast('Trade deleted', 'success');
        }
    },

    /**
     * Close modal
     */
    close() {
        this.modal?.classList.remove('active');
        this.currentDate = null;
        this.editingTradeId = null;
    },

    /**
     * Check if trades exist for a date
     */
    hasTradesForDate(date) {
        return Storage.getTrades(date).length > 0;
    }
};

// Make TradeForm available globally
window.TradeForm = TradeForm;
