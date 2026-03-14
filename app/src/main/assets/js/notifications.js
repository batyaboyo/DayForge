/**
 * DayForge - Notifications Module
 * Browser notification system for block reminders
 */

const Notifications = {
    scheduledNotifications: [],
    permissionGranted: false,

    /**
     * Initialize notifications
     */
    async init() {
        this.permissionGranted = Notification.permission === 'granted';

        if (Storage.getNotificationsEnabled() && this.permissionGranted) {
            this.scheduleAllNotifications();
        }

        this.updateButtonState();
    },

    /**
     * Request notification permission
     */
    async requestPermission() {
        if (!('Notification' in window)) {
            console.warn('This browser does not support notifications');
            return false;
        }

        if (Notification.permission === 'granted') {
            this.permissionGranted = true;
            return true;
        }

        if (Notification.permission !== 'denied') {
            const permission = await Notification.requestPermission();
            this.permissionGranted = permission === 'granted';
            return this.permissionGranted;
        }

        return false;
    },

    /**
     * Toggle notifications on/off
     */
    async toggle() {
        const currentlyEnabled = Storage.getNotificationsEnabled();

        if (!currentlyEnabled) {
            // Trying to enable
            const hasPermission = await this.requestPermission();
            if (hasPermission) {
                Storage.setNotificationsEnabled(true);
                this.scheduleAllNotifications();
                App.showToast('Notifications enabled', 'success');
            } else {
                App.showToast('Notification permission denied', 'warning');
            }
        } else {
            // Disable
            Storage.setNotificationsEnabled(false);
            this.clearAllNotifications();
            App.showToast('Notifications disabled', 'success');
        }

        this.updateButtonState();
    },

    /**
     * Update notification button visual state
     */
    updateButtonState() {
        const btn = document.getElementById('notificationBtn');
        const icon = btn?.querySelector('.notification-icon');

        if (btn && icon) {
            const enabled = Storage.getNotificationsEnabled() && this.permissionGranted;
            icon.textContent = enabled ? '🔔' : '🔕';
            btn.classList.toggle('active', enabled);
        }
    },

    /**
     * Schedule notifications for all blocks today
     */
    scheduleAllNotifications() {
        this.clearAllNotifications();

        const schedule = Data.getScheduleForDate(new Date());
        const now = new Date();

        schedule.forEach(block => {
            const [hours, minutes] = block.time.split(':').map(Number);
            const notificationTime = new Date();
            notificationTime.setHours(hours, minutes, 0, 0);

            // Only schedule if in the future
            if (notificationTime > now) {
                const delay = notificationTime.getTime() - now.getTime();

                // Schedule 5 minutes before
                if (delay > 5 * 60 * 1000) {
                    const reminderDelay = delay - (5 * 60 * 1000);
                    const reminderId = setTimeout(() => {
                        this.showNotification(
                            `Starting in 5 minutes: ${block.title}`,
                            block.purpose
                        );
                    }, reminderDelay);
                    this.scheduledNotifications.push(reminderId);
                }

                // Schedule at start time
                const startId = setTimeout(() => {
                    this.showNotification(
                        `Time to start: ${block.title}`,
                        block.purpose
                    );
                }, delay);
                this.scheduledNotifications.push(startId);
            }
        });
    },

    /**
     * Clear all scheduled notifications
     */
    clearAllNotifications() {
        this.scheduledNotifications.forEach(id => clearTimeout(id));
        this.scheduledNotifications = [];
    },

    /**
     * Show a notification
     */
    showNotification(title, body) {
        if (!this.permissionGranted || !Storage.getNotificationsEnabled()) {
            return;
        }

        try {
            const notification = new Notification(title, {
                body: body,
                icon: '⚡',
                badge: '⚡',
                tag: 'dayforge-block',
                requireInteraction: false
            });

            // Auto close after 10 seconds
            setTimeout(() => notification.close(), 10000);

            notification.onclick = () => {
                window.focus();
                notification.close();
            };
        } catch (e) {
            console.error('Error showing notification:', e);
        }
    }
};

// Make Notifications available globally
window.Notifications = Notifications;
