/**
 * DayForge - Summary Dashboard Module
 * Weekly summary with stats, patterns, and recommendations
 */

const SummaryDashboard = {
    currentWeekStart: null,

    /**
     * Initialize summary dashboard
     */
    init() {
        this.currentWeekStart = WeeklyGoals.getWeekStart();
        this.bindEvents();
    },

    /**
     * Bind event listeners
     */
    bindEvents() {
        document.getElementById('prevWeek')?.addEventListener('click', () => this.navigateWeek(-1));
        document.getElementById('nextWeek')?.addEventListener('click', () => this.navigateWeek(1));
    },

    /**
     * Navigate to a different week
     */
    navigateWeek(delta) {
        const newWeekStart = new Date(this.currentWeekStart);
        newWeekStart.setDate(newWeekStart.getDate() + (delta * 7));
        this.currentWeekStart = newWeekStart;
        this.updateWeekDisplay();
        this.render();
    },

    /**
     * Update week range display
     */
    updateWeekDisplay() {
        const weekEnd = new Date(this.currentWeekStart);
        weekEnd.setDate(weekEnd.getDate() + 6);

        const options = { month: 'short', day: 'numeric' };
        const startStr = this.currentWeekStart.toLocaleDateString('en-US', options);
        const endStr = weekEnd.toLocaleDateString('en-US', options);

        document.getElementById('weekRange').textContent = `${startStr} - ${endStr}`;
    },

    /**
     * Render the summary dashboard
     */
    render() {
        const container = document.getElementById('summaryContainer');
        if (!container) return;

        this.updateWeekDisplay();

        const stats = Data.getWeeklyStats(this.currentWeekStart);
        const goals = Storage.getWeeklyGoals();
        const recommendations = Recommendations.generate(this.currentWeekStart);

        container.innerHTML = `
            ${this.renderStatsCards(stats, goals)}
            
            <div class="summary-grid">
                ${this.renderSkipPatterns(stats)}
                ${this.renderRecommendations(recommendations)}
            </div>

            ${this.renderSkipReasons(stats)}
        `;
    },

    /**
     * Render stat cards
     */
    renderStatsCards(stats, goals) {
        const completionRate = stats.totalBlocks > 0
            ? Math.round((stats.completedBlocks / stats.totalBlocks) * 100)
            : 0;

        return `
            <div class="summary-stats">
                <div class="stat-card">
                    <div class="stat-value">${completionRate}%</div>
                    <div class="stat-label">Completion Rate</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">${stats.completedBlocks}/${stats.totalBlocks}</div>
                    <div class="stat-label">Blocks Completed</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">${stats.studyHoursCybersecurity + stats.studyHoursBlockchain}h</div>
                    <div class="stat-label">Study Hours</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">${stats.paperTrades}</div>
                    <div class="stat-label">Paper Trades</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">${stats.workouts}/7</div>
                    <div class="stat-label">Workout Days</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">${stats.mealsCooked}</div>
                    <div class="stat-label">Meals Cooked</div>
                </div>
            </div>
        `;
    },

    /**
     * Render most skipped blocks
     */
    renderSkipPatterns(stats) {
        const sortedSkips = Object.entries(stats.mostSkippedBlocks)
            .sort(([, a], [, b]) => b - a)
            .slice(0, 5);

        if (sortedSkips.length === 0) {
            return `
                <div class="summary-section">
                    <h3>🎯 Most Skipped Blocks</h3>
                    <div class="empty-state">
                        <p>No skipped blocks this week. Amazing discipline!</p>
                    </div>
                </div>
            `;
        }

        return `
            <div class="summary-section">
                <h3>⚠️ Most Skipped Blocks</h3>
                <div class="skip-patterns">
                    ${sortedSkips.map(([block, count]) => `
                        <div class="skip-item">
                            <span class="skip-item-name">${block}</span>
                            <span class="skip-item-count">${count}x skipped</span>
                        </div>
                    `).join('')}
                </div>
            </div>
        `;
    },

    /**
     * Render recommendations
     */
    renderRecommendations(recommendations) {
        return `
            <div class="summary-section">
                <h3>💡 Weekly Recommendations</h3>
                <div class="recommendations">
                    ${recommendations.map(rec => Recommendations.getRecommendationHTML(rec)).join('')}
                </div>
            </div>
        `;
    },

    /**
     * Render skip reasons summary
     */
    renderSkipReasons(stats) {
        if (stats.skipReasons.length === 0) {
            return '';
        }

        // Group reasons by common themes
        const recentReasons = stats.skipReasons.slice(-10);

        return `
            <div class="summary-section">
                <h3>📝 Recent Skip Reasons</h3>
                <div class="skip-reasons-list">
                    ${recentReasons.map(item => `
                        <div class="skip-reason-item">
                            <span class="skip-reason-block">${item.block}</span>
                            <span class="skip-reason-date">${item.date}</span>
                            <p class="skip-reason-text">"${item.reason}"</p>
                        </div>
                    `).join('')}
                </div>
            </div>
        `;
    }
};

// Make SummaryDashboard available globally
window.SummaryDashboard = SummaryDashboard;
