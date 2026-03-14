/**
 * DayForge - Review Screen Module
 * End-of-day review forcing reflection on unfinished blocks
 */

const ReviewScreen = {
    /**
     * Initialize review screen
     */
    init() {
        // Review screen is rendered when navigated to
    },

    /**
     * Render the review screen
     */
    render(date = DailyView.getCurrentDate()) {
        const container = document.getElementById('reviewContainer');
        if (!container) return;

        const schedule = Data.getScheduleForDate(date);
        const stats = Data.getDailyStats(date);
        const trades = Storage.getTrades(date);

        // Check which blocks need reflection
        const needsReflection = schedule.filter(block =>
            (block.status === 'skipped' || block.status === 'not-started' ||
                (block.didIt === true && block.finishedIt === false)) &&
            !block.skipReason
        );

        const isReviewComplete = needsReflection.length === 0;

        container.innerHTML = `
            ${this.renderSummary(stats, schedule.length)}
            
            <div class="review-section">
                <h3>Block Status Overview</h3>
                <div class="review-blocks">
                    ${schedule.map(block => this.renderReviewBlock(block, date)).join('')}
                </div>
            </div>

            ${this.renderTradesSummary(trades, date)}

            ${needsReflection.length > 0 ? `
                <div class="review-pending">
                    <h3>⚠️ Reflection Required</h3>
                    <p>${needsReflection.length} block(s) need reflection before completing your review.</p>
                    <div class="pending-blocks">
                        ${needsReflection.map(block => `
                            <button class="btn btn-warning reflect-btn" data-block-id="${block.id}">
                                Reflect: ${block.title}
                            </button>
                        `).join('')}
                    </div>
                </div>
            ` : `
                <div class="review-complete">
                    <div class="review-complete-icon">✓</div>
                    <h3>Day Review Complete!</h3>
                    <p>You've reflected on all blocks. Great job being honest with yourself!</p>
                    <div class="review-score">
                        <span class="score-value">${Math.round((stats.finished / schedule.length) * 100)}%</span>
                        <span class="score-label">Completion Rate</span>
                    </div>
                </div>
            `}
        `;

        // Update review status
        document.getElementById('reviewStatus').textContent =
            isReviewComplete ? '✓ Complete' : `${needsReflection.length} pending`;
        document.getElementById('reviewStatus').className =
            `review-status ${isReviewComplete ? 'complete' : 'pending'}`;

        // Bind reflection buttons
        container.querySelectorAll('.reflect-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                const blockId = btn.dataset.blockId;
                const block = schedule.find(b => b.id === blockId);
                // Use actual status, but treat 'not-started' as 'skipped' for the reflection
                const status = block.status === 'not-started' ? 'skipped' : block.status;
                SkipModal.open(blockId, date, { 
                    status: status, 
                    didIt: block.didIt, 
                    finishedIt: block.finishedIt 
                });
            });
        });

        // Bind review block clicks
        container.querySelectorAll('.review-block').forEach(el => {
            el.addEventListener('click', () => {
                const blockId = el.dataset.blockId;
                BlockModal.open(blockId, date);
            });
        });
    },

    /**
     * Render summary stats
     */
    renderSummary(stats, total) {
        return `
            <div class="review-summary">
                <div class="review-stat">
                    <div class="review-stat-value finished">${stats.finished}</div>
                    <div class="review-stat-label">Finished</div>
                </div>
                <div class="review-stat">
                    <div class="review-stat-value in-progress">${stats.inProgress}</div>
                    <div class="review-stat-label">In Progress</div>
                </div>
                <div class="review-stat">
                    <div class="review-stat-value skipped">${stats.skipped}</div>
                    <div class="review-stat-label">Skipped</div>
                </div>
                <div class="review-stat">
                    <div class="review-stat-value not-started">${stats.notStarted}</div>
                    <div class="review-stat-label">Not Started</div>
                </div>
            </div>
        `;
    },

    /**
     * Render a single review block
     */
    renderReviewBlock(block, date) {
        const needsReflection = (block.status === 'skipped' || block.status === 'not-started' ||
            (block.didIt === true && block.finishedIt === false)) && !block.skipReason;

        return `
            <div class="review-block ${needsReflection ? 'needs-reflection' : ''}" 
                 data-block-id="${block.id}">
                <div class="review-block-status ${block.status}"></div>
                <div class="review-block-info">
                    <div class="review-block-title">${block.title}</div>
                    <div class="review-block-time">${DailyView.formatTime(block.time)}</div>
                    ${block.skipReason ? `
                        <div class="review-block-reason">"${block.skipReason}"</div>
                    ` : ''}
                </div>
                <div class="review-action">
                    <span class="block-status ${block.status}">${DailyView.formatStatus(block.status)}</span>
                </div>
            </div>
        `;
    },

    /**
     * Render trades summary
     */
    renderTradesSummary(trades, date) {
        if (trades.length === 0) {
            return `
                <div class="review-section trades-section">
                    <h3>📈 Paper Trades</h3>
                    <div class="empty-state">
                        <p>No paper trades logged today.</p>
                        <button class="btn btn-primary open-trade-modal">Log a Trade</button>
                    </div>
                </div>
            `;
        }

        const wins = trades.filter(t => t.result === 'win').length;
        const losses = trades.filter(t => t.result === 'loss').length;
        const pending = trades.filter(t => t.result === 'pending').length;

        return `
            <div class="review-section trades-section">
                <h3>📈 Paper Trades (${trades.length})</h3>
                <div class="trades-stats">
                    <span class="trade-stat win">${wins} Wins</span>
                    <span class="trade-stat loss">${losses} Losses</span>
                    <span class="trade-stat pending">${pending} Pending</span>
                </div>
                <div class="trades-list-mini">
                    ${trades.map(trade => `
                        <div class="trade-mini">
                            <span class="trade-asset">${trade.asset}</span>
                            <span class="trade-item-bias ${trade.bias}">${trade.bias.toUpperCase()}</span>
                            <span class="trade-item-result ${trade.result}">${trade.result.toUpperCase()}</span>
                        </div>
                    `).join('')}
                </div>
            </div>
        `;
    }
};

// Make ReviewScreen available globally
window.ReviewScreen = ReviewScreen;
