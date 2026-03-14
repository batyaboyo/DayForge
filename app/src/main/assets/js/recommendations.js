/**
 * DayForge - Recommendations Module
 * Rule-based weekly recommendation engine
 */

const Recommendations = {
    /**
     * Generate recommendations based on weekly data
     */
    generate(weekStartDate) {
        const stats = Data.getWeeklyStats(weekStartDate);
        const goals = Storage.getWeeklyGoals();
        const recommendations = [];

        // Analyze skip patterns
        const skipAnalysis = this.analyzeSkipPatterns(stats);

        // Check for burnout patterns
        const burnoutCheck = this.checkBurnoutPatterns(stats);

        // Goal-based recommendations
        const goalRecommendations = this.getGoalRecommendations(stats, goals);

        // Combine all recommendations
        recommendations.push(...skipAnalysis);
        recommendations.push(...burnoutCheck);
        recommendations.push(...goalRecommendations);

        // Add positive reinforcement if doing well
        if (recommendations.length === 0) {
            recommendations.push({
                type: 'success',
                icon: '🌟',
                title: 'Great Week!',
                message: 'You\'re staying consistent. Keep up the excellent work and maintain this momentum.'
            });
        }

        return recommendations;
    },

    /**
     * Analyze skip patterns and suggest time adjustments
     */
    analyzeSkipPatterns(stats) {
        const recommendations = [];
        const sortedSkips = Object.entries(stats.mostSkippedBlocks)
            .sort(([, a], [, b]) => b - a);

        // If a block is skipped more than 3 times in a week
        sortedSkips.forEach(([blockTitle, count]) => {
            if (count >= 3) {
                recommendations.push({
                    type: 'warning',
                    icon: '⏰',
                    title: `Adjust ${blockTitle}`,
                    message: `You've skipped "${blockTitle}" ${count} times this week. Consider moving it to a different time slot or breaking it into smaller chunks.`
                });
            }
        });

        // Analyze common skip reasons
        const reasonPatterns = this.findReasonPatterns(stats.skipReasons);
        reasonPatterns.forEach(pattern => {
            recommendations.push({
                type: 'info',
                icon: '💡',
                title: 'Pattern Detected',
                message: pattern
            });
        });

        return recommendations;
    },

    /**
     * Find patterns in skip reasons
     */
    findReasonPatterns(skipReasons) {
        const patterns = [];
        const reasonKeywords = {};

        // Common keywords to look for
        const keywords = ['tired', 'busy', 'forgot', 'sick', 'work', 'time', 'late', 'early', 'motivation'];

        skipReasons.forEach(({ reason }) => {
            const lowerReason = reason.toLowerCase();
            keywords.forEach(keyword => {
                if (lowerReason.includes(keyword)) {
                    reasonKeywords[keyword] = (reasonKeywords[keyword] || 0) + 1;
                }
            });
        });

        // Generate insights from keywords
        if (reasonKeywords['tired'] >= 3) {
            patterns.push('You mentioned being "tired" frequently. Consider earlier bedtime or evaluating your sleep quality.');
        }
        if (reasonKeywords['time'] >= 3 || reasonKeywords['busy'] >= 3) {
            patterns.push('Time constraints are a recurring issue. Try time-blocking or reducing scope of some activities.');
        }
        if (reasonKeywords['motivation'] >= 2) {
            patterns.push('Motivation appears to be a challenge. Consider reviewing your "why" and celebrating small wins.');
        }
        if (reasonKeywords['forgot'] >= 2) {
            patterns.push('Forgetting tasks is happening. Enable notifications and place visual reminders in your environment.');
        }

        return patterns;
    },

    /**
     * Check for burnout patterns
     */
    checkBurnoutPatterns(stats) {
        const recommendations = [];

        // High skip rate (more than 40% of blocks skipped)
        const skipRate = stats.skippedBlocks / stats.totalBlocks;
        if (skipRate > 0.4) {
            recommendations.push({
                type: 'caution',
                icon: '🔥',
                title: 'Potential Burnout Detected',
                message: `You've skipped ${Math.round(skipRate * 100)}% of your blocks this week. Consider reducing your load and focusing on just 2-3 essential blocks per day until you recover.`
            });
        }

        // Consistency over perfection
        if (stats.completedBlocks > 0 && skipRate > 0.3 && skipRate <= 0.4) {
            recommendations.push({
                type: 'tip',
                icon: '🎯',
                title: 'Aim for Consistency',
                message: 'Perfection isn\'t the goal—consistency is. Try completing just 80% of blocks consistently rather than 100% sporadically.'
            });
        }

        return recommendations;
    },

    /**
     * Get goal-based recommendations
     */
    getGoalRecommendations(stats, goals) {
        const recommendations = [];

        // Study hours
        const totalStudyHours = stats.studyHoursCybersecurity + stats.studyHoursBlockchain;
        const targetStudyHours = goals.studyHoursCybersecurity + goals.studyHoursBlockchain;

        if (totalStudyHours < targetStudyHours * 0.5) {
            recommendations.push({
                type: 'warning',
                icon: '📚',
                title: 'Study Hours Behind',
                message: `You've logged ${totalStudyHours}h of your ${targetStudyHours}h goal. Block distractions and protect your deep study time.`
            });
        }

        // Paper trades
        if (stats.paperTrades < goals.paperTrades * 0.5) {
            recommendations.push({
                type: 'info',
                icon: '📈',
                title: 'More Paper Trades Needed',
                message: `${stats.paperTrades} of ${goals.paperTrades} paper trades completed. Even a quick 5-min market scan counts!`
            });
        }

        // Workouts
        if (stats.workouts < goals.workouts * 0.5) {
            recommendations.push({
                type: 'tip',
                icon: '💪',
                title: 'Get Moving',
                message: `${stats.workouts} of ${goals.workouts} workouts done. Physical health powers mental performance.`
            });
        }

        // Meals
        const expectedMeals = goals.mealsCookedPerDay * 7;
        if (stats.mealsCooked < expectedMeals * 0.5) {
            recommendations.push({
                type: 'tip',
                icon: '🍳',
                title: 'Cook More',
                message: 'Meal prep on weekends can help you hit your cooking goals during busy weekdays.'
            });
        }

        return recommendations;
    },

    /**
     * Get recommendation card HTML
     */
    getRecommendationHTML(recommendation) {
        return `
            <div class="recommendation-card ${recommendation.type}">
                <div class="recommendation-icon">${recommendation.icon}</div>
                <div class="recommendation-content">
                    <h4>${recommendation.title}</h4>
                    <p>${recommendation.message}</p>
                </div>
            </div>
        `;
    }
};

// Make Recommendations available globally
window.Recommendations = Recommendations;
