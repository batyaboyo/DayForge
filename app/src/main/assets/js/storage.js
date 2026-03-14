/**
 * DayForge - Storage Module
 * LocalStorage abstraction layer for offline data persistence
 */

const Storage = {
    // Storage keys
    KEYS: {
        THEME: 'dayforge_theme',
        SCHEDULES: 'dayforge_schedules',
        TRADES: 'dayforge_trades',
        GOALS: 'dayforge_goals',
        NOTIFICATIONS_ENABLED: 'dayforge_notifications'
    },

    /**
     * Get data from localStorage
     */
    get(key) {
        try {
            const data = localStorage.getItem(key);
            return data ? JSON.parse(data) : null;
        } catch (e) {
            console.error('Storage.get error:', e);
            return null;
        }
    },

    /**
     * Set data in localStorage
     */
    set(key, value) {
        try {
            localStorage.setItem(key, JSON.stringify(value));
            return true;
        } catch (e) {
            console.error('Storage.set error:', e);
            return false;
        }
    },

    /**
     * Remove data from localStorage
     */
    remove(key) {
        try {
            localStorage.removeItem(key);
            return true;
        } catch (e) {
            console.error('Storage.remove error:', e);
            return false;
        }
    },

    // ==========================================
    // Theme Management
    // ==========================================

    getTheme() {
        return this.get(this.KEYS.THEME) || 'light';
    },

    setTheme(theme) {
        return this.set(this.KEYS.THEME, theme);
    },

    // ==========================================
    // Schedule Management (by date)
    // ==========================================

    /**
     * Get date key in YYYY-MM-DD format
     */
    getDateKey(date = new Date()) {
        return date.toISOString().split('T')[0];
    },

    /**
     * Get schedule for a specific date
     */
    getSchedule(date = new Date()) {
        const schedules = this.get(this.KEYS.SCHEDULES) || {};
        const dateKey = this.getDateKey(date);
        return schedules[dateKey] || null;
    },

    /**
     * Save schedule for a specific date
     */
    saveSchedule(schedule, date = new Date()) {
        const schedules = this.get(this.KEYS.SCHEDULES) || {};
        const dateKey = this.getDateKey(date);
        schedules[dateKey] = schedule;
        return this.set(this.KEYS.SCHEDULES, schedules);
    },

    /**
     * Get all schedules (for weekly calculations)
     */
    getAllSchedules() {
        return this.get(this.KEYS.SCHEDULES) || {};
    },

    /**
     * Get schedules for a week (Monday to Sunday)
     */
    getWeekSchedules(weekStartDate) {
        const schedules = this.getAllSchedules();
        const weekSchedules = {};

        for (let i = 0; i < 7; i++) {
            const date = new Date(weekStartDate);
            date.setDate(date.getDate() + i);
            const dateKey = this.getDateKey(date);
            if (schedules[dateKey]) {
                weekSchedules[dateKey] = schedules[dateKey];
            }
        }

        return weekSchedules;
    },

    // ==========================================
    // Paper Trades Management
    // ==========================================

    /**
     * Get trades for a specific date
     */
    getTrades(date = new Date()) {
        const trades = this.get(this.KEYS.TRADES) || {};
        const dateKey = this.getDateKey(date);
        return trades[dateKey] || [];
    },

    /**
     * Save a trade for a specific date
     */
    saveTrade(trade, date = new Date()) {
        const trades = this.get(this.KEYS.TRADES) || {};
        const dateKey = this.getDateKey(date);

        if (!trades[dateKey]) {
            trades[dateKey] = [];
        }

        trade.id = Date.now();
        trade.createdAt = new Date().toISOString();
        trades[dateKey].push(trade);

        return this.set(this.KEYS.TRADES, trades);
    },

    /**
     * Update a trade
     */
    updateTrade(tradeId, updates, date = new Date()) {
        const trades = this.get(this.KEYS.TRADES) || {};
        const dateKey = this.getDateKey(date);

        if (trades[dateKey]) {
            const index = trades[dateKey].findIndex(t => t.id === tradeId);
            if (index !== -1) {
                trades[dateKey][index] = { ...trades[dateKey][index], ...updates };
                return this.set(this.KEYS.TRADES, trades);
            }
        }
        return false;
    },

    /**
     * Delete a trade
     */
    deleteTrade(tradeId, date = new Date()) {
        const trades = this.get(this.KEYS.TRADES) || {};
        const dateKey = this.getDateKey(date);

        if (trades[dateKey]) {
            trades[dateKey] = trades[dateKey].filter(t => t.id !== tradeId);
            return this.set(this.KEYS.TRADES, trades);
        }
        return false;
    },

    /**
     * Get all trades (for weekly calculations)
     */
    getAllTrades() {
        return this.get(this.KEYS.TRADES) || {};
    },

    /**
     * Get trades for a week
     */
    getWeekTrades(weekStartDate) {
        const allTrades = this.getAllTrades();
        const weekTrades = [];

        for (let i = 0; i < 7; i++) {
            const date = new Date(weekStartDate);
            date.setDate(date.getDate() + i);
            const dateKey = this.getDateKey(date);
            if (allTrades[dateKey]) {
                weekTrades.push(...allTrades[dateKey]);
            }
        }

        return weekTrades;
    },

    // ==========================================
    // Weekly Goals Management
    // ==========================================

    /**
     * Get weekly goals
     */
    getWeeklyGoals() {
        return this.get(this.KEYS.GOALS) || {
            studyHoursCybersecurity: 10,
            studyHoursBlockchain: 10,
            projectsCompleted: 2,
            paperTrades: 7,
            workouts: 5,
            mealsCookedPerDay: 3,
            sleepHours: 7
        };
    },

    /**
     * Save weekly goals
     */
    saveWeeklyGoals(goals) {
        return this.set(this.KEYS.GOALS, goals);
    },

    // ==========================================
    // Notifications Settings
    // ==========================================

    getNotificationsEnabled() {
        return this.get(this.KEYS.NOTIFICATIONS_ENABLED) || false;
    },

    setNotificationsEnabled(enabled) {
        return this.set(this.KEYS.NOTIFICATIONS_ENABLED, enabled);
    },

    // ==========================================
    // Trading Journal Management
    // ==========================================

    /**
     * Get trading journal for a specific date
     */
    getTradingJournal(date = new Date()) {
        const journals = this.get('dayforge_trading_journals') || {};
        const dateKey = this.getDateKey(date);
        return journals[dateKey] || {
            lessonsLearned: '',
            mistakes: '',
            observations: '',
            improvements: ''
        };
    },

    /**
     * Save trading journal for a specific date
     */
    saveTradingJournal(journal, date = new Date()) {
        const journals = this.get('dayforge_trading_journals') || {};
        const dateKey = this.getDateKey(date);
        journals[dateKey] = {
            ...journal,
            updatedAt: new Date().toISOString()
        };
        return this.set('dayforge_trading_journals', journals);
    },

    /**
     * Get all trading journals for weekly summary
     */
    getWeekTradingJournals(weekStartDate) {
        const allJournals = this.get('dayforge_trading_journals') || {};
        const weekJournals = [];

        for (let i = 0; i < 7; i++) {
            const date = new Date(weekStartDate);
            date.setDate(date.getDate() + i);
            const dateKey = this.getDateKey(date);
            if (allJournals[dateKey]) {
                weekJournals.push({
                    date: dateKey,
                    ...allJournals[dateKey]
                });
            }
        }

        return weekJournals;
    },

    // ==========================================
    // Daily Journal Management
    // ==========================================

    /**
     * Get daily journal for a specific date
     */
    getDailyJournal(date = new Date()) {
        const journals = this.get('dayforge_daily_journals') || {};
        const dateKey = this.getDateKey(date);
        return journals[dateKey] || {
            morning: '',
            evening: '',
            thoughts: '',
            emotionalState: '',
            gratitude: ''
        };
    },

    /**
     * Save daily journal for a specific date
     */
    saveDailyJournal(journal, date = new Date()) {
        const journals = this.get('dayforge_daily_journals') || {};
        const dateKey = this.getDateKey(date);
        journals[dateKey] = {
            ...journal,
            updatedAt: new Date().toISOString()
        };
        return this.set('dayforge_daily_journals', journals);
    }
};

// Make Storage available globally
window.Storage = Storage;
