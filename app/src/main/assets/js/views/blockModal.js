/**
 * DayForge - Block Modal Module
 * Handles block detail editing modal
 */

const BlockModal = {
    currentBlockId: null,
    currentDate: null,
    modal: null,

    /**
     * Initialize modal
     */
    init() {
        this.modal = document.getElementById('blockModal');
        this.bindEvents();
    },

    /**
     * Bind event listeners
     */
    bindEvents() {
        document.getElementById('modalClose')?.addEventListener('click', () => this.close());
        document.getElementById('modalCancel')?.addEventListener('click', () => this.close());
        document.getElementById('modalSave')?.addEventListener('click', () => this.save());

        // Close on overlay click
        this.modal?.addEventListener('click', (e) => {
            if (e.target === this.modal) {
                this.close();
            }
        });

        // Close on escape key
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && this.modal?.classList.contains('active')) {
                this.close();
            }
        });
    },

    /**
     * Open modal for a block
     */
    open(blockId, date) {
        this.currentBlockId = blockId;
        this.currentDate = date;

        const block = Data.getBlock(blockId, date);
        if (!block) return;

        // Update modal title
        document.getElementById('modalTitle').textContent = block.title;

        // Render modal body
        const body = document.getElementById('modalBody');
        body.innerHTML = this.renderModalContent(block);

        // Bind status selector
        body.querySelectorAll('.status-option').forEach(option => {
            option.addEventListener('click', () => {
                body.querySelectorAll('.status-option').forEach(o => o.classList.remove('selected'));
                option.classList.add('selected');
            });
        });

        // Show/hide trade button for trading blocks
        if (block.requiresTrade) {
            const tradeBtn = body.querySelector('.open-trade-modal');
            tradeBtn?.addEventListener('click', () => {
                this.close();
                TradeForm.open(date);
            });
        }

        // Bind trading journal button
        if (block.isTradingJournal) {
            const journalBtn = body.querySelector('.open-trading-journal');
            journalBtn?.addEventListener('click', () => {
                this.close();
                TradingJournalModal.open(date);
            });
        }

        // Bind daily journal button
        if (block.isJournal) {
            const journalBtn = body.querySelector('.open-daily-journal');
            journalBtn?.addEventListener('click', () => {
                const journalType = journalBtn.dataset.journalType;
                this.close();
                DailyJournalModal.open(date, journalType);
            });
        }

        // Show modal
        this.modal.classList.add('active');
    },

    /**
     * Render modal content
     */
    renderModalContent(block) {
        const trades = Storage.getTrades(this.currentDate);
        const hasTrades = trades.length > 0;

        return `
            <div class="modal-block-info">
                <div class="form-group">
                    <label>Time</label>
                    <p class="block-time-display">${DailyView.formatTime(block.time)} - ${DailyView.formatTime(block.endTime)}</p>
                </div>
                
                <div class="form-group">
                    <label>Purpose</label>
                    <p class="block-purpose-display">${block.purpose}</p>
                </div>

                <div class="form-group">
                    <label>Status</label>
                    <div class="status-selector">
                        <button class="status-option not-started ${block.status === 'not-started' ? 'selected' : ''}" data-status="not-started">
                            Not Started
                        </button>
                        <button class="status-option in-progress ${block.status === 'in-progress' ? 'selected' : ''}" data-status="in-progress">
                            In Progress
                        </button>
                        <button class="status-option finished ${block.status === 'finished' ? 'selected' : ''}" data-status="finished">
                            Finished
                        </button>
                        <button class="status-option skipped ${block.status === 'skipped' ? 'selected' : ''}" data-status="skipped">
                            Skipped
                        </button>
                    </div>
                </div>

                <div class="form-group">
                    <label>Did I do it?</label>
                    <div class="toggle-group">
                        <label class="toggle-switch">
                            <input type="checkbox" id="didItToggle" ${block.didIt ? 'checked' : ''}>
                            <span class="toggle-slider"></span>
                        </label>
                        <span class="toggle-label">${block.didIt ? 'Yes' : 'No'}</span>
                    </div>
                </div>

                <div class="form-group">
                    <label>Did I finish it?</label>
                    <div class="toggle-group">
                        <label class="toggle-switch">
                            <input type="checkbox" id="finishedItToggle" ${block.finishedIt ? 'checked' : ''}>
                            <span class="toggle-slider"></span>
                        </label>
                        <span class="toggle-label">${block.finishedIt ? 'Yes' : 'No'}</span>
                    </div>
                </div>

                <div class="form-group">
                    <label>Notes / Reflections</label>
                    <textarea id="blockNotes" rows="3" placeholder="Notes about this activity...">${block.notes || ''}</textarea>
                </div>

                ${block.requiresTrade ? `
                    <div class="form-group trade-section">
                        <label>Paper Trades</label>
                        <div class="trade-status ${hasTrades ? 'has-trades' : 'no-trades'}">
                            ${hasTrades
                    ? `<span class="trade-count">✓ ${trades.length} trade(s) logged today</span>`
                    : '<span class="trade-warning">⚠️ No trades logged yet</span>'}
                        </div>
                        <button class="btn btn-secondary open-trade-modal">
                            ${hasTrades ? 'View / Add Trades' : 'Log a Trade'}
                        </button>
                    </div>
                ` : ''}

                ${block.isTradingJournal ? `
                    <div class="form-group journal-section">
                        <label>📈 Trading Journal</label>
                        <p class="journal-hint">Document lessons learned, mistakes, and observations from today's trades.</p>
                        <button class="btn btn-primary open-trading-journal">Open Trading Journal</button>
                    </div>
                ` : ''}

                ${block.isJournal ? `
                    <div class="form-group journal-section">
                        <label>📝 ${block.journalType === 'morning' ? 'Morning' : 'Evening'} Journal</label>
                        <p class="journal-hint">${block.journalType === 'morning'
                    ? 'Set intentions, gratitude, and goals for the day.'
                    : 'Reflect on the day. What worked? What didn\'t?'}</p>
                        <button class="btn btn-primary open-daily-journal" data-journal-type="${block.journalType}">
                            Open Journal
                        </button>
                    </div>
                ` : ''}

                ${block.skipReason ? `
                    <div class="form-group">
                        <label>Skip Reason</label>
                        <p class="skip-reason-display">${block.skipReason}</p>
                    </div>
                ` : ''}
            </div>
        `;
    },


    /**
     * Save changes
     */
    save() {
        const body = document.getElementById('modalBody');
        const selectedStatus = body.querySelector('.status-option.selected')?.dataset.status;
        const didIt = body.querySelector('#didItToggle')?.checked;
        const finishedIt = body.querySelector('#finishedItToggle')?.checked;
        const notes = body.querySelector('#blockNotes')?.value || '';

        // Check if skipped or unfinished - require reason
        if (selectedStatus === 'skipped' || (didIt && !finishedIt)) {
            const block = Data.getBlock(this.currentBlockId, this.currentDate);
            if (!block.skipReason) {
                // Save notes first
                Data.updateBlock(this.currentBlockId, { notes }, this.currentDate);
                this.close();
                SkipModal.open(this.currentBlockId, this.currentDate, {
                    status: selectedStatus,
                    didIt,
                    finishedIt
                });
                return;
            }
        }

        // Save updates
        const updates = {
            status: selectedStatus,
            didIt,
            finishedIt,
            notes
        };

        Data.updateBlock(this.currentBlockId, updates, this.currentDate);

        this.close();
        DailyView.render();
        App.showToast('Block updated', 'success');
    },

    /**
     * Close modal
     */
    close() {
        this.modal?.classList.remove('active');
        this.currentBlockId = null;
        this.currentDate = null;
    }
};

/**
 * Skip Reason Modal
 */
const SkipModal = {
    currentBlockId: null,
    currentDate: null,
    pendingUpdates: null,
    modal: null,

    init() {
        this.modal = document.getElementById('skipModal');
        this.bindEvents();
    },

    bindEvents() {
        document.getElementById('skipModalClose')?.addEventListener('click', () => this.close());
        document.getElementById('skipModalCancel')?.addEventListener('click', () => this.close());
        document.getElementById('skipModalSave')?.addEventListener('click', () => this.save());

        this.modal?.addEventListener('click', (e) => {
            if (e.target === this.modal) {
                this.close();
            }
        });
    },

    open(blockId, date, pendingUpdates) {
        this.currentBlockId = blockId;
        this.currentDate = date;
        this.pendingUpdates = pendingUpdates;

        const block = Data.getBlock(blockId, date);
        document.getElementById('skipModalTitle').textContent =
            `Why didn't you ${pendingUpdates.status === 'skipped' ? 'complete' : 'finish'} "${block.title}"?`;

        document.getElementById('skipReason').value = block.skipReason || '';

        this.modal.classList.add('active');
        document.getElementById('skipReason').focus();
    },

    save() {
        const reason = document.getElementById('skipReason').value.trim();

        if (!reason) {
            App.showToast('Please provide a reason', 'warning');
            return;
        }

        const updates = {
            ...this.pendingUpdates,
            skipReason: reason
        };

        Data.updateBlock(this.currentBlockId, updates, this.currentDate);

        this.close();
        DailyView.render();
        App.showToast('Block updated with reflection', 'success');
    },

    close() {
        this.modal?.classList.remove('active');
        this.currentBlockId = null;
        this.currentDate = null;
        this.pendingUpdates = null;
    }
};

// Make modules available globally
window.BlockModal = BlockModal;
window.SkipModal = SkipModal;
