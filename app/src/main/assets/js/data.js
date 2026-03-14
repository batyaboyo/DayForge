/**
 * DayForge - Data Module
 * Default schedule blocks and data structures
 */

const Data = {
    // Block categories for styling and grouping
    CATEGORIES: {
        WAKE: 'wake',
        SPIRITUAL: 'spiritual',
        FITNESS: 'fitness',
        MEALS: 'meals',
        STUDY: 'study',
        TRADING: 'trading',
        PROJECTS: 'projects',
        LEISURE: 'leisure',
        REFLECTION: 'reflection',
        JOURNAL: 'journal',
        SLEEP: 'sleep'
    },

    // Block statuses
    STATUS: {
        NOT_STARTED: 'not-started',
        IN_PROGRESS: 'in-progress',
        FINISHED: 'finished',
        SKIPPED: 'skipped'
    },

    /**
     * Default schedule blocks template
     * This is used when no schedule exists for a date
     */
    getDefaultBlocks() {
        return [
            {
                id: 'wake',
                time: '06:00',
                endTime: '06:30',
                title: 'Wake',
                purpose: 'Start the day with intention. Hydrate, stretch, and prepare mentally.',
                category: this.CATEGORIES.WAKE,
                status: this.STATUS.NOT_STARTED,
                didIt: null,
                finishedIt: null,
                skipReason: '',
                notes: ''
            },
            {
                id: 'morning-journal',
                time: '06:30',
                endTime: '06:45',
                title: 'Morning Journal',
                purpose: 'Set intentions, gratitude, and goals for the day. Clear your mind.',
                category: this.CATEGORIES.JOURNAL,
                status: this.STATUS.NOT_STARTED,
                didIt: null,
                finishedIt: null,
                skipReason: '',
                notes: '',
                isJournal: true,
                journalType: 'morning'
            },
            {
                id: 'prayer',
                time: '06:45',
                endTime: '07:00',
                title: 'Prayer / Meditation',
                purpose: 'Center yourself. Connect with your purpose and set intentions.',
                category: this.CATEGORIES.SPIRITUAL,
                status: this.STATUS.NOT_STARTED,
                didIt: null,
                finishedIt: null,
                skipReason: '',
                notes: ''
            },
            {
                id: 'workout',
                time: '07:00',
                endTime: '08:00',
                title: 'Workout',
                purpose: 'Build physical strength and discipline. Energy for the day.',
                category: this.CATEGORIES.FITNESS,
                status: this.STATUS.NOT_STARTED,
                didIt: null,
                finishedIt: null,
                skipReason: '',
                notes: ''
            },
            {
                id: 'breakfast',
                time: '08:00',
                endTime: '08:30',
                title: 'Cook & Eat Breakfast',
                purpose: 'Fuel your body. Practice cooking skills. Mindful eating.',
                category: this.CATEGORIES.MEALS,
                status: this.STATUS.NOT_STARTED,
                didIt: null,
                finishedIt: null,
                skipReason: '',
                notes: ''
            },
            {
                id: 'deep-study-1',
                time: '09:00',
                endTime: '12:00',
                title: 'Deep Study 1: Cybersecurity',
                purpose: 'Master security concepts. Labs, certifications, hands-on practice.',
                category: this.CATEGORIES.STUDY,
                status: this.STATUS.NOT_STARTED,
                didIt: null,
                finishedIt: null,
                skipReason: '',
                notes: '',
                studyHours: 3,
                studyType: 'cybersecurity'
            },
            {
                id: 'lunch',
                time: '12:00',
                endTime: '13:00',
                title: 'Cook & Eat Lunch',
                purpose: 'Nourish and recharge. Break from screen. Prepare healthy meal.',
                category: this.CATEGORIES.MEALS,
                status: this.STATUS.NOT_STARTED,
                didIt: null,
                finishedIt: null,
                skipReason: '',
                notes: ''
            },
            {
                id: 'deep-study-2',
                time: '13:00',
                endTime: '16:00',
                title: 'Deep Study 2: Blockchain',
                purpose: 'Understand decentralized systems. Smart contracts, protocols, development.',
                category: this.CATEGORIES.STUDY,
                status: this.STATUS.NOT_STARTED,
                didIt: null,
                finishedIt: null,
                skipReason: '',
                notes: '',
                studyHours: 3,
                studyType: 'blockchain'
            },
            {
                id: 'projects',
                time: '16:00',
                endTime: '17:30',
                title: 'Projects / Labs',
                purpose: 'Apply knowledge. Build portfolio. Real-world implementation.',
                category: this.CATEGORIES.PROJECTS,
                status: this.STATUS.NOT_STARTED,
                didIt: null,
                finishedIt: null,
                skipReason: '',
                notes: ''
            },
            {
                id: 'trading-scan',
                time: '17:30',
                endTime: '18:30',
                title: 'Trading Scan + Paper Trade',
                purpose: 'Analyze markets. Identify setups. Execute at least one paper trade.',
                category: this.CATEGORIES.TRADING,
                status: this.STATUS.NOT_STARTED,
                didIt: null,
                finishedIt: null,
                skipReason: '',
                notes: '',
                requiresTrade: true
            },
            {
                id: 'dinner',
                time: '18:30',
                endTime: '19:30',
                title: 'Cook & Eat Dinner',
                purpose: 'End-of-day nourishment. Cooking as meditation. Quality meal.',
                category: this.CATEGORIES.MEALS,
                status: this.STATUS.NOT_STARTED,
                didIt: null,
                finishedIt: null,
                skipReason: '',
                notes: ''
            },
            {
                id: 'walk',
                time: '19:30',
                endTime: '20:00',
                title: 'Walk',
                purpose: 'Movement and fresh air. Process the day. Clear your mind.',
                category: this.CATEGORIES.LEISURE,
                status: this.STATUS.NOT_STARTED,
                didIt: null,
                finishedIt: null,
                skipReason: '',
                notes: ''
            },
            {
                id: 'trading-review',
                time: '20:00',
                endTime: '20:30',
                title: 'Trading Review + Journal',
                purpose: 'Review trades. Document lessons learned. Refine strategy.',
                category: this.CATEGORIES.TRADING,
                status: this.STATUS.NOT_STARTED,
                didIt: null,
                finishedIt: null,
                skipReason: '',
                notes: '',
                isTradingJournal: true
            },
            {
                id: 'reading',
                time: '20:30',
                endTime: '21:30',
                title: 'Reading',
                purpose: 'Expand knowledge. Books on trading, tech, psychology, or personal growth.',
                category: this.CATEGORIES.LEISURE,
                status: this.STATUS.NOT_STARTED,
                didIt: null,
                finishedIt: null,
                skipReason: '',
                notes: ''
            },
            {
                id: 'evening-journal',
                time: '21:30',
                endTime: '21:45',
                title: 'Evening Journal',
                purpose: 'Reflect on the day. What worked? What didn\'t? Emotional check-in.',
                category: this.CATEGORIES.JOURNAL,
                status: this.STATUS.NOT_STARTED,
                didIt: null,
                finishedIt: null,
                skipReason: '',
                notes: '',
                isJournal: true,
                journalType: 'evening'
            },
            {
                id: 'reflection',
                time: '21:45',
                endTime: '22:00',
                title: 'Evening Reflection',
                purpose: 'Review the day\'s progress. Plan tomorrow\'s priorities.',
                category: this.CATEGORIES.REFLECTION,
                status: this.STATUS.NOT_STARTED,
                didIt: null,
                finishedIt: null,
                skipReason: '',
                notes: ''
            },
            {
                id: 'wind-down',
                time: '22:00',
                endTime: '22:30',
                title: 'Wind Down',
                purpose: 'Prepare for sleep. No screens. Relaxation routine.',
                category: this.CATEGORIES.LEISURE,
                status: this.STATUS.NOT_STARTED,
                didIt: null,
                finishedIt: null,
                skipReason: '',
                notes: ''
            },
            {
                id: 'sleep',
                time: '22:30',
                endTime: '06:00',
                title: 'Sleep',
                purpose: 'Recovery and consolidation. 7-8 hours for optimal performance.',
                category: this.CATEGORIES.SLEEP,
                status: this.STATUS.NOT_STARTED,
                didIt: null,
                finishedIt: null,
                skipReason: '',
                notes: ''
            }
        ];
    },


    /**
     * Get schedule for a date (from storage or default)
     */
    getScheduleForDate(date = new Date()) {
        const saved = Storage.getSchedule(date);
        if (saved) {
            return saved;
        }
        // Return a new copy of default blocks
        return this.getDefaultBlocks();
    },

    /**
     * Save schedule for a date
     */
    saveScheduleForDate(schedule, date = new Date()) {
        return Storage.saveSchedule(schedule, date);
    },

    /**
     * Update a specific block in the schedule
     */
    updateBlock(blockId, updates, date = new Date()) {
        const schedule = this.getScheduleForDate(date);
        const index = schedule.findIndex(b => b.id === blockId);

        if (index !== -1) {
            schedule[index] = { ...schedule[index], ...updates };
            return this.saveScheduleForDate(schedule, date);
        }
        return false;
    },

    /**
     * Get block by ID for a specific date
     */
    getBlock(blockId, date = new Date()) {
        const schedule = this.getScheduleForDate(date);
        return schedule.find(b => b.id === blockId) || null;
    },

    /**
     * Calculate daily stats
     */
    getDailyStats(date = new Date()) {
        const schedule = this.getScheduleForDate(date);

        const stats = {
            total: schedule.length,
            notStarted: 0,
            inProgress: 0,
            finished: 0,
            skipped: 0,
            studyHoursCybersecurity: 0,
            studyHoursBlockchain: 0,
            mealsCooked: 0,
            workedOut: false,
            tradingDone: false
        };

        schedule.forEach(block => {
            switch (block.status) {
                case this.STATUS.NOT_STARTED:
                    stats.notStarted++;
                    break;
                case this.STATUS.IN_PROGRESS:
                    stats.inProgress++;
                    break;
                case this.STATUS.FINISHED:
                    stats.finished++;
                    // Track specific completions
                    if (block.studyType === 'cybersecurity' && block.studyHours) {
                        stats.studyHoursCybersecurity += block.studyHours;
                    }
                    if (block.studyType === 'blockchain' && block.studyHours) {
                        stats.studyHoursBlockchain += block.studyHours;
                    }
                    if (block.category === this.CATEGORIES.MEALS) {
                        stats.mealsCooked++;
                    }
                    if (block.id === 'workout') {
                        stats.workedOut = true;
                    }
                    if (block.category === this.CATEGORIES.TRADING) {
                        stats.tradingDone = true;
                    }
                    break;
                case this.STATUS.SKIPPED:
                    stats.skipped++;
                    break;
            }
        });

        return stats;
    },

    /**
     * Get weekly stats
     */
    getWeeklyStats(weekStartDate) {
        const stats = {
            totalBlocks: 0,
            completedBlocks: 0,
            skippedBlocks: 0,
            studyHoursCybersecurity: 0,
            studyHoursBlockchain: 0,
            paperTrades: 0,
            workouts: 0,
            mealsCooked: 0,
            skipReasons: [],
            mostSkippedBlocks: {}
        };

        for (let i = 0; i < 7; i++) {
            const date = new Date(weekStartDate);
            date.setDate(date.getDate() + i);

            const dailyStats = this.getDailyStats(date);
            const schedule = this.getScheduleForDate(date);
            const trades = Storage.getTrades(date);

            stats.totalBlocks += dailyStats.total;
            stats.completedBlocks += dailyStats.finished;
            stats.skippedBlocks += dailyStats.skipped;
            stats.studyHoursCybersecurity += dailyStats.studyHoursCybersecurity;
            stats.studyHoursBlockchain += dailyStats.studyHoursBlockchain;
            stats.paperTrades += trades.length;
            if (dailyStats.workedOut) stats.workouts++;
            stats.mealsCooked += dailyStats.mealsCooked;

            // Collect skip reasons and most skipped blocks
            schedule.forEach(block => {
                if (block.status === this.STATUS.SKIPPED) {
                    if (block.skipReason) {
                        stats.skipReasons.push({
                            block: block.title,
                            reason: block.skipReason,
                            date: Storage.getDateKey(date)
                        });
                    }
                    stats.mostSkippedBlocks[block.title] = (stats.mostSkippedBlocks[block.title] || 0) + 1;
                }
            });
        }

        return stats;
    },

    /**
     * Get the current time block (or next upcoming)
     */
    getCurrentBlock(date = new Date()) {
        const schedule = this.getScheduleForDate(date);
        const now = new Date();
        const currentTime = now.getHours() * 60 + now.getMinutes();

        for (const block of schedule) {
            const [hours, minutes] = block.time.split(':').map(Number);
            const [endHours, endMinutes] = block.endTime.split(':').map(Number);
            const blockStart = hours * 60 + minutes;
            let blockEnd = endHours * 60 + endMinutes;

            // Handle overnight blocks (like sleep)
            if (blockEnd < blockStart) {
                blockEnd += 24 * 60;
            }

            if (currentTime >= blockStart && currentTime < blockEnd) {
                return block;
            }
        }

        return null;
    }
};

// Make Data available globally
window.Data = Data;
