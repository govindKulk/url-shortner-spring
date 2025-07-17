import { TokenResponse, AuthResponse, LoginRequest, RegisterRequest, CreateUrlRequest, CreateShortUrlResponse, UrlMapping } from '@/types'

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080'

class ApiService {
  private getAuthHeaders(): HeadersInit {
    const token = localStorage.getItem('accessToken')
    return {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` }),
    }
  }

  private getUserIdHeader(): HeadersInit {
    const userId = localStorage.getItem('userId')
    return {
      'Content-Type': 'application/json',
      ...(userId && { 'X-User-ID': userId }),
    }
  }

  // Auth endpoints
  async login(credentials: LoginRequest): Promise<TokenResponse> {
    const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(credentials),
    })

    if (!response.ok) {
      throw new Error('Login failed')
    }

    return response.json()
  }

  async register(userData: RegisterRequest): Promise<TokenResponse> {
    const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(userData),
    })

    if (!response.ok) {
      throw new Error('Registration failed')
    }

    return response.json()
  }

  async getCurrentUser(): Promise<AuthResponse> {
    const response = await fetch(`${API_BASE_URL}/api/auth/me`, {
      headers: this.getAuthHeaders(),
    })

    if (!response.ok) {
      throw new Error('Failed to get user info')
    }

    return response.json()
  }

  async validateToken(): Promise<AuthResponse> {
    const response = await fetch(`${API_BASE_URL}/api/auth/validate`, {
      method: 'POST',
      headers: this.getAuthHeaders(),
    })

    if (!response.ok) {
      throw new Error('Token validation failed')
    }

    return response.json()
  }

  // URL endpoints
  async createShortUrl(urlData: CreateUrlRequest): Promise<CreateShortUrlResponse> {
    const response = await fetch(`${API_BASE_URL}/api/urls`, {
      method: 'POST',
      headers: {...this.getUserIdHeader(), ...this.getAuthHeaders()},
      body: JSON.stringify(urlData),
    })

    if (!response.ok) {
      throw new Error('Failed to create short URL')
    }

    return response.json()
  }

  async getUserUrls(): Promise<UrlMapping[]> {
    const response = await fetch(`${API_BASE_URL}/api/urls`, {
      headers: this.getUserIdHeader(),
    })

    if (!response.ok) {
      throw new Error('Failed to fetch URLs')
    }

    return response.json()
  }

  async deleteUrl(shortUrl: string): Promise<void> {
    const response = await fetch(`${API_BASE_URL}/api/urls/${shortUrl}`, {
      method: 'DELETE',
      headers: this.getUserIdHeader(),
    })

    if (!response.ok) {
      throw new Error('Failed to delete URL')
    }
  }

  async getUrlStats(shortUrl: string): Promise<UrlMapping> {
    const response = await fetch(`${API_BASE_URL}/api/urls/stats/${shortUrl}`, {
      headers: this.getUserIdHeader(),
    })

    if (!response.ok) {
      throw new Error('Failed to fetch URL stats')
    }

    return response.json()
  }

  async getRedirectUrl(shortUrl: string): Promise<{originalUrl: string}> {
    const response = await fetch(`${API_BASE_URL}/api/urls/${shortUrl}`);
    const body = await response.json();
    return body;


  }
}

export const apiService = new ApiService() 