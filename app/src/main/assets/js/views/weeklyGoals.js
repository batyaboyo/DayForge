/**
 * DayForge - Weekly Goals Module
 * Manages weekly goals and tracks progress
 */

const WeeklyGoals = {
    isEditing: false,

    /**
     * Initialize weekly goals
     */
    init() {
        this.bindEvents();
    },

    /**
     * Bind event listeners
     */
    bindEvents() {
        document.getElementById('editGoalsBtn')?.addEventListener('click', () => this.toggleEdit());
    },

    /**
     * Get current week start date (Monday)
     */
    getWeekStart(date = new Date()) {
        const d = new Date(date);
        const day = d.getDay();
        const diff = d.getDate() - day + (day === 0 ? -6 : 1);
        d.setDate(diff);
        d.setHours(0, 0, 0, 0);
        return d;
    },

    /**
     * Render weekly goals view
     */
    render() {
        const container = document.getElementById('goalsContainer');
        if (!container) return;

        const goals = Storage.getWeeklyGoals();
        const weekStart = this.getWeekStart();
        const stats = Data.getWeeklyStats(weekStart);

        const goalCards = [
            {
                id: 'studyHoursCybersecurity',
                icon: '🔐',
                title: 'Cybersecurity Study',
                target: goals.studyHoursCybersecurity,
                current: stats.studyHoursCybersecurity,
                unit: 'hours'
            },
            {
                id: 'studyHoursBlockchain',
                icon: '⛓️',
                title: 'Blockchain Study',
                target: goals.studyHoursBlockchain,
                current: stats.studyHoursBlockchain,
                unit: 'hours'
            },
            {
                id: 'projectsCompleted',
                icon: '🚀',
                title: 'Projects Completed',
                target: goals.projectsCompleted,
                current: this.getProjectsCompleted(weekStart),
                unit: 'projects'
            },
            {
                id: 'paperTrades',
                icon: '📈',
                title: 'Paper Trades',
                target: goals.paperTrades,
                current: stats.paperTrades,
                unit: 'trades'
            },
            {
                id: 'workouts',
                icon: '💪',
                title: 'Workouts',
                target: goals.workouts,
                current: stats.workouts,
                unit: 'sessions'
            },
            {
                id: 'mealsCooked',
                icon: '🍳',
                title: 'Meals Cooked',
                target: goals.mealsCookedPerDay * 7,
                current: stats.mealsCooked,
                unit: 'meals'
            }
        ];

        container.innerHTML = goalCards.map(goal => this.renderGoalCard(goal)).join('');

        // Bind edit inputs if in edit mode
        if (this.isEditing) {
            container.querySelectorAll('.goal-input').forEach(input => {
                input.addEventListener('change', (e) => {
                    this.updateGoal(input.dataset.goalId, parseInt(e.target.value));
                });
            });
        }
    },

    /**
     * Render a single goal card
     */
    renderGoalCard(goal) {
        const percentage = Math.min(100, Math.round((goal.current / goal.target) * 100));
        const isComplete = goal.current >= goal.target;

        return `
            <div class="goal-card ${isComplete ? 'complete' : ''}">
                <div class="goal-header">
                    <div class="goal-icon">${goal.icon}</div>
                    <div class="goal-info">
                        <h3>${goal.title}</h3>
                        <span>${goal.current} / ${this.isEditing ?
                `<input type="number" class="goal-input" data-goal-id="${goal.id}" value="${goal.target}" min="1">` :
                goal.target} ${goal.unit}</span>
                    </div>
                </div>
                <div class="goal-progress">
                    <div class="progress-bar">
                        <div class="progress-fill" style="width: ${percentage}%"></div>
                    </div>
                    <div class="progress-text">
                        <span>${percentage}% complete</span>
                        <span>${isComplete ? '✓ Goal reached!' : `${goal.target - goal.current} ${goal.unit} to go`}</span>
                    </div>
                </div>
            </div>
        `;
    },

    /**
     * Count projects completed this week
     */
    getProjectsCompleted(weekStart) {
        let count = 0;
        for (let i = 0; i < 7; i++) {
            const date = new Date(weekStart);
            date.setDate(date.getDate() + i);
            const schedule = Data.getScheduleForDate(date);
            const projectBlock = schedule.find(b => b.id === 'projects');
            if (projectBlock && projectBlock.status === Data.STATUS.FINISHED) {
                count++;
            }
        }
        return Math.floor(count / 2); // Roughly 1 project per 2 completed project sessions
    },

    /**
     * Toggle edit mode
     */
    toggleEdit() {
        this.isEditing = !this.isEditing;
        document.getElementById('editGoalsBtn').textContent = this.isEditing ? 'Save Goals' : 'Edit Goals';
        this.render();

        if (!this.isEditing) {
            App.showToast('Goals saved', 'success');
        }
    },

    /**
     * Update a specific goal
     */
    updateGoal(goalId, value) {
        const goals = Storage.getWeeklyGoals();
        if (goalId === 'mealsCooked') {
            goals.mealsCookedPerDay = Math.ceil(value / 7);
        } else {
            goals[goalId] = value;
        }
        Storage.saveWeeklyGoals(goals);
    }
};

// Make WeeklyGoals available globally
window.WeeklyGoals = WeeklyGoals;
