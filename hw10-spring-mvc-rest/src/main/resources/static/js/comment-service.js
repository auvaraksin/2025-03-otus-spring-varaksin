class CommentService {
    constructor() {
        this.apiClient = apiClient;
    }

    async loadComments(bookId) {
        try {
            console.log('Loading comments for book:', bookId);
            const comments = await this.apiClient.get(`/book/${bookId}/comments`);
            console.log('Comments received:', comments);
            this.renderComments(comments);
        } catch (error) {
            console.error('Error loading comments:', error);
            showError('Error loading comments: ' + error.message);
        }
    }

    async addComment(bookId, text) {
        try {
            await this.apiClient.post('/comments', {
                bookId: bookId,
                text: text
            });
            await this.loadComments(bookId);
            document.getElementById('comment-text').value = '';
        } catch (error) {
            showError('Error adding comment: ' + error.message);
        }
    }

    async deleteComment(commentId, bookId) {
        if (confirm('Are you sure you want to delete this comment?')) {
            try {
                await this.apiClient.delete(`/comments/${commentId}`);
                await this.loadComments(bookId);
            } catch (error) {
                showError('Error deleting comment: ' + error.message);
            }
        }
    }

    renderComments(comments) {
        const container = document.getElementById('comments-list');
        if (comments.length === 0) {
            container.innerHTML = '<p>No comments yet.</p>';
            return;
        }

        container.innerHTML = comments.map(comment => `
            <div style="border: 1px solid #eee; padding: 10px; margin: 5px 0;">
                <p>${this.escapeHtml(comment.text)}</p>
                <button onclick="commentService.deleteComment(${comment.id}, ${comment.bookId || comment.book?.id || bookId})">Delete</button>
            </div>
        `).join('');
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

const commentService = new CommentService();

// Обработчик формы комментариев
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('comment-form');
    if (form) {
        form.addEventListener('submit', function(e) {
            e.preventDefault();

            const bookId = document.getElementById('book-id').value;
            const text = document.getElementById('comment-text').value;

            if (text.trim()) {
                commentService.addComment(bookId, text.trim());
            }
        });
    }
});
