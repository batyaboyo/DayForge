/**
 * DayForge - Daily View Module
 * Renders and manages the daily schedule view
 */

const DailyView = {
    currentDate: new Date(),

    /**
     * Initialize daily view
     */
    init() {
        this.bindEvents();
        this.render();
        this.updateDateDisplay();
    },

    /**
     * Bind event listeners
     */
    bindEvents() {
        // Date navigation
        document.getElementById('prevDay')?.addEventListener('click', () => this.navigateDay(-1));
        document.getElementById('nextDay')?.addEventListener('click', () => this.navigateDay(1));
        document.getElementById('todayBtn')?.addEventListener('click', () => this.goToToday());
    },

    /**
     * Navigate to a different day
     */
    navigateDay(delta) {
        this.currentDate.setDate(this.currentDate.getDate() + delta);
        this.updateDateDisplay();
        this.render();
    },

    /**
     * Go to today
     */
    goToToday() {
        this.currentDate = new Date();
        this.updateDateDisplay();
        this.render();
    },

    /**
     * Update the date display
     */
    updateDateDisplay() {
        const dateDisplay = document.getElementById('currentDate');
        if (!dateDisplay) return;

        const options = { weekday: 'long' };
        const dayName = this.currentDate.toLocaleDateString('en-US', options);

        const fullOptions = { year: 'numeric', month: 'long', day: 'numeric' };
        const fullDate = this.currentDate.toLocaleDateString('en-US', fullOptions);

        dateDisplay.innerHTML = `
            <span class="date-day">${dayName}</span>
            <span class="date-full">${fullDate}</span>
        `;

        // Show/hide today button
        const todayBtn = document.getElementById('todayBtn');
        if (todayBtn) {
            const isToday = Storage.getDateKey(this.currentDate) === Storage.getDateKey(new Date());
            todayBtn.style.display = isToday ? 'none' : 'inline-block';
        }
    },

    /**
     * Render the schedule
     */
    render() {
        const container = document.getElementById('scheduleContainer');
        if (!container) return;

        const schedule = Data.getScheduleForDate(this.currentDate);
        const currentBlock = Data.getCurrentBlock(this.currentDate);
        const isToday = Storage.getDateKey(this.currentDate) === Storage.getDateKey(new Date());

        container.innerHTML = schedule.map(block => this.renderBlock(block, currentBlock, isToday)).join('');

        // Bind block click events
        container.querySelectorAll('.schedule-block').forEach(el => {
            el.addEventListener('click', () => {
                const blockId = el.dataset.blockId;
                BlockModal.open(blockId, this.currentDate);
            });
        });

        // Update progress
        this.updateProgress(schedule);
    },

    /**
     * Render a single block
     */
    renderBlock(block, currentBlock, isToday) {
        const isCurrent = isToday && currentBlock && currentBlock.id === block.id;
        const statusClass = block.status.replace('-', '-');

        // Did I do it / Did I finish it display
        let checkDisplay = '';
        if (block.didIt !== null || block.finishedIt !== null) {
            checkDisplay = `
                <div class="block-check">
                    ${block.didIt !== null ? `
                        <span class="check-item ${block.didIt ? 'yes' : 'no'}">
                            ${block.didIt ? '✓' : '✗'} Did it
                        </span>
                    ` : ''}
                    ${block.finishedIt !== null ? `
                        <span class="check-item ${block.finishedIt ? 'yes' : 'no'}">
                            ${block.finishedIt ? '✓' : '✗'} Finished
                        </span>
                    ` : ''}
                </div>
            `;
        }

        return `
            <div class="schedule-block ${isCurrent ? 'current' : ''}" 
                 data-block-id="${block.id}" 
                 data-status="${block.status}">
                <div class="block-time">${this.formatTime(block.time)}</div>
                <div class="block-content">
                    <div class="block-title">${block.title}</div>
                    <div class="block-purpose">${block.purpose}</div>
                </div>
                <div class="block-actions">
                    <span class="block-status ${block.status}">${this.formatStatus(block.status)}</span>
                    ${checkDisplay}
                </div>
            </div>
        `;
    },

    /**
     * Format time display (24h to 12h)
     */
    formatTime(time) {
        const [hours, minutes] = time.split(':').map(Number);
        const period = hours >= 12 ? 'PM' : 'AM';
        const displayHours = hours % 12 || 12;
        return `${displayHours}:${minutes.toString().padStart(2, '0')} ${period}`;
    },

    /**
     * Format status for display
     */
    formatStatus(status) {
        const labels = {
            'not-started': 'Not Started',
            'in-progress': 'In Progress',
            'finished': 'Finished',
            'skipped': 'Skipped'
        };
        return labels[status] || status;
    },

    /**
     * Update progress indicator
     */
    updateProgress(schedule) {
        const progressEl = document.getElementById('dailyProgress');
        if (!progressEl) return;

        const finished = schedule.filter(b => b.status === Data.STATUS.FINISHED).length;
        const total = schedule.length;

        progressEl.innerHTML = `
            <span class="progress-count">${finished}/${total}</span>
            <span class="progress-label">completed</span>
        `;
    },

    /**
     * Get current viewing date
     */
    getCurrentDate() {
        return this.currentDate;
    }
};

// Make DailyView available globally
window.DailyView = DailyView;
