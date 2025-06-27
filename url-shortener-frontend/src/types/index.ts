export interface TokenResponse {
  access_token: string
  refresh_token: string
  token_type: string
  expires_in: number
}

export interface AuthResponse {
  success: boolean
  message: string
  username?: string
  role?: string
  token?: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  email: string
}

export interface CreateUrlRequest {
  originalUrl: string
}

export interface CreateShortUrlResponse {
  shortUrl: string
  originalUrl: string
  success: boolean
  message: string
}

export interface UrlMapping {
  id: number
  userId: number
  originalUrl: string
  shortUrl: string
  createdAt: string
  expirationDate: string
  clickCount: number
}

export interface User {
  id: number
  username: string
  email: string
  role: string
} 