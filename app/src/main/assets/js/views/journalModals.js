/**
 * DayForge - Journal Modals Module
 * Handles Trading Journal and Daily Journal modals
 */

/**
 * Trading Journal Modal
 */
const TradingJournalModal = {
    modal: null,
    currentDate: null,

    init() {
        this.modal = document.getElementById('tradingJournalModal');
        this.bindEvents();
    },

    bindEvents() {
        document.getElementById('tradingJournalClose')?.addEventListener('click', () => this.close());
        document.getElementById('tradingJournalCancel')?.addEventListener('click', () => this.close());
        document.getElementById('tradingJournalSave')?.addEventListener('click', () => this.save());

        this.modal?.addEventListener('click', (e) => {
            if (e.target === this.modal) {
                this.close();
            }
        });
    },

    open(date = new Date()) {
        this.currentDate = date;

        // Load existing journal
        const journal = Storage.getTradingJournal(date);

        document.getElementById('tradingLessons').value = journal.lessonsLearned || '';
        document.getElementById('tradingMistakes').value = journal.mistakes || '';
        document.getElementById('tradingObservations').value = journal.observations || '';
        document.getElementById('tradingImprovements').value = journal.improvements || '';

        this.modal.classList.add('active');
    },

    save() {
        const journal = {
            lessonsLearned: document.getElementById('tradingLessons').value.trim(),
            mistakes: document.getElementById('tradingMistakes').value.trim(),
            observations: document.getElementById('tradingObservations').value.trim(),
            improvements: document.getElementById('tradingImprovements').value.trim()
        };

        Storage.saveTradingJournal(journal, this.currentDate);

        this.close();
        App.showToast('Trading journal saved', 'success');
    },

    close() {
        this.modal?.classList.remove('active');
        this.currentDate = null;
    }
};

/**
 * Daily Journal Modal
 */
const DailyJournalModal = {
    modal: null,
    currentDate: null,
    journalType: null,

    init() {
        this.modal = document.getElementById('dailyJournalModal');
        this.bindEvents();
    },

    bindEvents() {
        document.getElementById('dailyJournalClose')?.addEventListener('click', () => this.close());
        document.getElementById('dailyJournalCancel')?.addEventListener('click', () => this.close());
        document.getElementById('dailyJournalSave')?.addEventListener('click', () => this.save());

        this.modal?.addEventListener('click', (e) => {
            if (e.target === this.modal) {
                this.close();
            }
        });
    },

    open(date = new Date(), type = 'morning') {
        this.currentDate = date;
        this.journalType = type;

        // Update title
        document.getElementById('dailyJournalTitle').textContent =
            type === 'morning' ? '📝 Morning Journal' : '📝 Evening Journal';

        // Show/hide appropriate sections
        const morningSection = document.getElementById('morningSection');
        const eveningSection = document.getElementById('eveningSection');

        if (type === 'morning') {
            morningSection.style.display = 'block';
            eveningSection.style.display = 'none';
        } else {
            morningSection.style.display = 'none';
            eveningSection.style.display = 'block';
        }

        // Load existing journal
        const journal = Storage.getDailyJournal(date);

        document.getElementById('journalMorning').value = journal.morning || '';
        document.getElementById('journalEvening').value = journal.evening || '';
        document.getElementById('journalThoughts').value = journal.thoughts || '';
        document.getElementById('journalEmotional').value = journal.emotionalState || '';

        this.modal.classList.add('active');
    },

    save() {
        // Load existing to preserve other fields
        const existing = Storage.getDailyJournal(this.currentDate);

        const journal = {
            ...existing,
            morning: document.getElementById('journalMorning').value.trim(),
            evening: document.getElementById('journalEvening').value.trim(),
            thoughts: document.getElementById('journalThoughts').value.trim(),
            emotionalState: document.getElementById('journalEmotional').value.trim()
        };

        Storage.saveDailyJournal(journal, this.currentDate);

        this.close();
        App.showToast('Journal saved', 'success');
    },

    close() {
        this.modal?.classList.remove('active');
        this.currentDate = null;
        this.journalType = null;
    }
};

// Make modules available globally
window.TradingJournalModal = TradingJournalModal;
window.DailyJournalModal = DailyJournalModal;
