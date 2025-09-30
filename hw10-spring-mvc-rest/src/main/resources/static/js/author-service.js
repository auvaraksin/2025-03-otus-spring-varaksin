class AuthorService {
    constructor() {
        this.apiClient = apiClient;
    }

    async loadAuthors() {
        try {
            const authors = await this.apiClient.get('/authors');
            this.populateAuthorSelect(authors);
        } catch (error) {
            console.error('Error loading authors:', error);
        }
    }

    populateAuthorSelect(authors) {
        const select = document.getElementById('author-select');
        authors.forEach(author => {
            const option = document.createElement('option');
            option.value = author.id;
            option.textContent = author.fullName;
            select.appendChild(option);
        });
    }
}

const authorService = new AuthorService();
