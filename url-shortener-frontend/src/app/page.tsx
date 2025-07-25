"use client"

import { useState, useEffect } from "react"
import { LoginForm } from "@/components/auth/LoginForm"
import { RegisterForm } from "@/components/auth/RegisterForm"
import { CreateUrlForm } from "@/components/url/CreateUrlForm"
import { UrlList } from "@/components/url/UrlList"
import { Button } from "@/components/ui/button"
import { LogOut, User } from "lucide-react"
import { toast } from "sonner"
import { jwtDecode, JwtPayload } from "jwt-decode"

export default function Home() {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [showRegister, setShowRegister] = useState(false)
  const [username, setUsername] = useState<string | null>(null)
  const [refreshKey, setRefreshKey] = useState(0)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const token = localStorage.getItem("accessToken")
    const storedUsername = localStorage.getItem("username")

    if (!token || !storedUsername) {
      setIsAuthenticated(false)
      setUsername(null)
      setLoading(false)
      return
    }

    // Check for cached user info (5 min cache)
    const cachedMe = sessionStorage.getItem("meCache")
    if (cachedMe) {
      const { data, timestamp } = JSON.parse(cachedMe)
      if (Date.now() - timestamp < 5 * 60 * 1000) {
        setIsAuthenticated(true)
        setUsername(data.username)
        setLoading(false)
        return
      } else {
        sessionStorage.removeItem("meCache")
      }
    }

    // Decode token and check expiry
    try {
      const decoded: JwtPayload = jwtDecode(token)
      if (decoded.exp && Date.now() >= decoded.exp * 1000) {
        // Token expired
        localStorage.removeItem("accessToken")
        localStorage.removeItem("refreshToken")
        localStorage.removeItem("username")
        localStorage.removeItem("userId")
        setIsAuthenticated(false)
        setUsername(null)
        setLoading(false)
        return
      }
    } catch (e) {
      // Invalid token
      localStorage.removeItem("accessToken")
      localStorage.removeItem("refreshToken")
      localStorage.removeItem("username")
      localStorage.removeItem("userId")
      setIsAuthenticated(false)
      setUsername(null)
      setLoading(false)
      return
    }

    // Validate token with backend
    fetch("http://localhost:8080/api/auth/me", {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(res => {
        if (!res.ok) throw new Error("Invalid token")
        return res.json()
      })
      .then(data => {
        setIsAuthenticated(true)
        setUsername(data.username)
        // Cache for 5 minutes
        sessionStorage.setItem("meCache", JSON.stringify({ data, timestamp: Date.now() }))
        setLoading(false)
      })
      .catch(() => {
        // Token invalid or expired
        localStorage.removeItem("accessToken")
        localStorage.removeItem("refreshToken")
        localStorage.removeItem("username")
        localStorage.removeItem("userId")
        setIsAuthenticated(false)
        setUsername(null)
        setLoading(false)
      })
  }, [])

  const handleAuthSuccess = () => {
    setIsAuthenticated(true)
    setUsername(localStorage.getItem("username"))
  }

  const handleLogout = () => {
    localStorage.removeItem("accessToken")
    localStorage.removeItem("refreshToken")
    localStorage.removeItem("username")
    localStorage.removeItem("userId")
    setIsAuthenticated(false)
    setUsername(null)
    toast.success("Logged out successfully!")
  }

  const handleUrlCreated = () => {
    setRefreshKey(prev => prev + 1)
  }

  if (!isAuthenticated) {
    return (
      <div className="min-h-screen bg-red-500 bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4 bg-red-500">
        <div className="w-full max-w-md">
          {showRegister ? (
            <RegisterForm
              onSuccess={handleAuthSuccess}
              onSwitchToLogin={() => setShowRegister(false)}
            />
          ) : (
            <LoginForm
              onSuccess={handleAuthSuccess}
              onSwitchToRegister={() => setShowRegister(true)}
            />
          )}
        </div>
      </div>
    )
  }

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-lg text-gray-600">Loading...</div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-semibold text-gray-900">URL Shortener</h1>
            </div>
            
            <div className="flex items-center gap-4">
              <div className="flex items-center gap-2 text-sm text-gray-600">
                <User className="h-4 w-4" />
                <span>{username}</span>
              </div>
              <Button
                onClick={handleLogout}
                variant="outline"
                size="sm"
                className="flex items-center gap-2"
              >
                <LogOut className="h-4 w-4" />
                Logout
              </Button>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid gap-8 lg:grid-cols-2">
          {/* Create URL Form */}
          <div>
            <CreateUrlForm onUrlCreated={handleUrlCreated} />
          </div>
          
          {/* URL List */}
          <div>
            <UrlList key={refreshKey} />
          </div>
        </div>
      </main>
    </div>
  )
}
