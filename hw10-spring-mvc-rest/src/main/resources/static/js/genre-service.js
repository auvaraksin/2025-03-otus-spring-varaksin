class GenreService {
    constructor() {
        this.apiClient = apiClient;
    }

    async loadGenres() {
        try {
            const genres = await this.apiClient.get('/genres');
            this.populateGenreSelect(genres);
        } catch (error) {
            console.error('Error loading genres:', error);
        }
    }

    populateGenreSelect(genres) {
        const select = document.getElementById('genre-select');
        genres.forEach(genre => {
            const option = document.createElement('option');
            option.value = genre.id;
            option.textContent = genre.name;
            select.appendChild(option);
        });
    }
}

const genreService = new GenreService();
