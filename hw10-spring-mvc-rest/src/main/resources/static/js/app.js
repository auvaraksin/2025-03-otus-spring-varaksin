class ApiClient {
    constructor() {
        this.baseUrl = '';
    }

    async get(url) {
        const response = await fetch(`${this.baseUrl}${url}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    }

    async post(url, data = null) {
        const config = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            }
        };

        if (data) {
            const params = new URLSearchParams();
            for (const key in data) {
                if (data[key] !== undefined && data[key] !== null) {
                    params.append(key, data[key].toString());
                }
            }
            config.body = params;
        }

        const response = await fetch(`${this.baseUrl}${url}`, config);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    }

    async put(url, data) {
        const params = new URLSearchParams();
        for (const key in data) {
            if (data[key] !== undefined && data[key] !== null) {
                params.append(key, data[key].toString());
            }
        }

        const response = await fetch(`${this.baseUrl}${url}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: params
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    }

    async delete(url) {
        const response = await fetch(`${this.baseUrl}${url}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        // Для DELETE запроса может не быть тела ответа
        if (response.status === 204) {
            return null;
        }
        return response.json();
    }
}

const apiClient = new ApiClient();

// Утилиты для работы с DOM
function showElement(id) {
    const element = document.getElementById(id);
    if (element) {
        element.style.display = 'block';
    }
}

function hideElement(id) {
    const element = document.getElementById(id);
    if (element) {
        element.style.display = 'none';
    }
}

function showError(message) {
    const errorDiv = document.getElementById('error');
    if (errorDiv) {
        errorDiv.textContent = message;
        showElement('error');
    } else {
        console.error('Error element not found:', message);
    }
}

function showLoading(show = true) {
    const loading = document.getElementById('loading');
    if (loading) {
        loading.style.display = show ? 'block' : 'none';
    }
}
