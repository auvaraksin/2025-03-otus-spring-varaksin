class BookService {
    constructor() {
        this.apiClient = apiClient;
    }

    async loadBooks() {
        try {
            hideElement('error');
            showElement('loading');
            hideElement('books-list');

            const books = await this.apiClient.get('/books');
            this.renderBooks(books);

            hideElement('loading');
            showElement('books-list');
        } catch (error) {
            hideElement('loading');
            showError('Error loading books: ' + error.message);
        }
    }

    async loadBookDetails(bookId) {
        try {
            const book = await this.apiClient.get(`/books/${bookId}`);
            this.renderBookDetails(book);
        } catch (error) {
            showError('Error loading book details: ' + error.message);
        }
    }

    async loadBookForEdit(bookId) {
        try {
            const book = await this.apiClient.get(`/books/${bookId}`);
            this.populateBookForm(book);
        } catch (error) {
            showError('Error loading book for edit: ' + error.message);
        }
    }

    async saveBook(bookData) {
        try {
            if (bookData.id) {
                // Для PUT запроса нужно передавать параметры отдельно, а не в теле объекта
                await this.apiClient.put(`/books/${bookData.id}`, {
                    title: bookData.title,
                    authorId: bookData.authorId,
                    genreId: bookData.genreId
                });
            } else {
                await this.apiClient.post('/books', {
                    title: bookData.title,
                    authorId: bookData.authorId,
                    genreId: bookData.genreId
                });
            }
            window.location.href = '/index.html';
        } catch (error) {
            showError('Error saving book: ' + error.message);
        }
    }

    async deleteBook(bookId) {
        if (confirm('Are you sure you want to delete this book?')) {
            try {
                await this.apiClient.delete(`/books/${bookId}`);
                await this.loadBooks();
            } catch (error) {
                showError('Error deleting book: ' + error.message);
            }
        }
    }

    renderBooks(books) {
        const container = document.getElementById('books-list');
        container.innerHTML = books.map(book => `
            <div class="book-item" style="border: 1px solid #ccc; padding: 10px; margin: 10px 0;">
                <h3>${this.escapeHtml(book.title)}</h3>
                <p>Author: ${this.escapeHtml(book.author.fullName)}</p>
                <p>Genre: ${this.escapeHtml(book.genre.name)}</p>
                <a href="/book-view.html?id=${book.id}">View</a> |
                <a href="/book-form.html?id=${book.id}">Edit</a> |
                <button onclick="bookService.deleteBook(${book.id})">Delete</button>
            </div>
        `).join('');
    }

    renderBookDetails(book) {
        document.getElementById('book-title').textContent = book.title;
        document.getElementById('book-author').textContent = book.author.fullName;
        document.getElementById('book-genre').textContent = book.genre.name;
        document.getElementById('book-id').value = book.id;
    }

    populateBookForm(book) {
        document.getElementById('form-title').textContent = 'Edit Book';
        document.getElementById('book-id').value = book.id;
        document.getElementById('book-title').value = book.title;
        document.getElementById('author-select').value = book.author.id;
        document.getElementById('genre-select').value = book.genre.id;
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

const bookService = new BookService();

// Обработчик формы
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('book-form');
    if (form) {
        form.addEventListener('submit', function(e) {
            e.preventDefault();

            const bookData = {
                title: document.getElementById('book-title').value,
                authorId: document.getElementById('author-select').value,
                genreId: document.getElementById('genre-select').value
            };

            const bookId = document.getElementById('book-id').value;
            if (bookId) {
                bookData.id = bookId;
            }

            bookService.saveBook(bookData);
        });
    }
});
