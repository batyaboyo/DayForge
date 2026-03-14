const App = {
    currentView: 'daily',

    init() {
        this.initTheme();
        DailyView.init();
        BlockModal.init();
        SkipModal.init();
        TradeForm.init();
        TradingJournalModal.init();
        DailyJournalModal.init();
        ReviewScreen.init();
        WeeklyGoals.init();
        SummaryDashboard.init();
        Notifications.init();
        this.bindThemeToggle();
        this.bindNotificationToggle();
    },

    navigateTo(view) {
        this.currentView = view;
        document.querySelectorAll('.nav-btn').forEach(btn => {
            btn.classList.toggle('active', btn.dataset.view === view);
        });
        document.querySelectorAll('.view').forEach(v => {
            v.classList.toggle('active', v.id === `${view}View`);
        });
        switch (view) {
            case 'daily':
                DailyView.render();
                break;
            case 'goals':
                WeeklyGoals.render();
                break;
            case 'summary':
                SummaryDashboard.render();
                break;
            case 'review':
                ReviewScreen.render();
                break;
        }
    },

    bindThemeToggle() {
        document.getElementById('themeToggle')?.addEventListener('click', () => {
            const currentTheme = Storage.getTheme();
            const newTheme = currentTheme === 'light' ? 'dark' : 'light';
            Storage.setTheme(newTheme);
            document.documentElement.setAttribute('data-theme', newTheme);
            this.updateThemeIcon(newTheme);
            this.showToast(`Switched to ${newTheme} mode`, 'success');
        });
    },

    updateThemeIcon(theme) {
        const icon = document.querySelector('.theme-icon');
        if (icon) {
            icon.textContent = theme === 'light' ? '🌙' : '☀️';
        }
    },

    bindNotificationToggle() {
        document.getElementById('notificationBtn')?.addEventListener('click', () => {
            Notifications.toggle();
        });
    },

    showToast(message, type = 'info') {
        const container = document.getElementById('toastContainer');
        if (!container) return;
        const icons = { success: '✓', warning: '⚠', error: '✗', info: 'ℹ' };
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.innerHTML = `<span class="toast-icon">${icons[type]}</span><span class="toast-message">${message}</span>`;
        container.appendChild(toast);
        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateX(100px)';
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    }
};

document.addEventListener('DOMContentLoaded', () => {
    App.init();
});

window.App = App;
