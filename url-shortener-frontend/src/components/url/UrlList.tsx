"use client"

import { useState, useEffect } from "react"
import { apiService } from "@/lib/api"
import { type UrlMapping } from "@/types"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { toast } from "sonner"
import { Loader2, Link, Copy, Check, Trash2, BarChart3, Calendar, MousePointer } from "lucide-react"
import { copyToClipboard, formatDate } from "@/lib/utils"

export function UrlList() {
  const [urls, setUrls] = useState<UrlMapping[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [copiedUrl, setCopiedUrl] = useState<string | null>(null)

  const fetchUrls = async () => {
    try {
      const data = await apiService.getUserUrls()
      setUrls(data)
    } catch (error) {
      toast.error("Failed to fetch URLs")
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    fetchUrls()
  }, [])

  const handleDelete = async (shortUrl: string) => {
    try {
      await apiService.deleteUrl(shortUrl)
      toast.success("URL deleted successfully!")
      fetchUrls() // Refresh the list
    } catch (error) {
      toast.error("Failed to delete URL")
    }
  }

  const handleCopy = async (url: string) => {
    copyToClipboard('http://localhost:3000/' + url)
    setCopiedUrl('http://localhost:3000/' + url)
    toast.success("URL copied to clipboard!")
    setTimeout(() => setCopiedUrl(null), 2000)
  }

  if (isLoading) {
    return (
      <Card className="w-full">
        <CardContent className="flex items-center justify-center py-8">
          <Loader2 className="h-6 w-6 animate-spin" />
        </CardContent>
      </Card>
    )
  }

  if (urls.length === 0) {
    return (
      <Card className="w-full">
        <CardContent className="flex flex-col items-center justify-center py-8 text-center">
          <Link className="h-12 w-12 text-gray-400 mb-4" />
          <h3 className="text-lg font-medium text-gray-900 mb-2">No URLs yet</h3>
          <p className="text-gray-500">Create your first short URL to get started!</p>
        </CardContent>
      </Card>
    )
  }

  return (
    <Card className="w-full">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <BarChart3 className="h-5 w-5" />
          Your URLs
        </CardTitle>
        <CardDescription>
          Manage and track your shortened URLs
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          {urls.map((url) => (
            <div
              key={url.id}
              className="border rounded-lg p-4 hover:bg-gray-50 transition-colors"
            >
              <div className="flex items-start justify-between gap-4">
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 mb-2">
                    <Link className="h-4 w-4 text-blue-600" />
                    <a
                      href={url.shortUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-blue-600 hover:underline font-medium truncate"
                    >
                      {url.shortUrl}
                    </a>
                  </div>
                  
                  <p className="text-sm text-gray-600 truncate mb-2">
                    {url.originalUrl}
                  </p>
                  
                  <div className="flex items-center gap-4 text-xs text-gray-500">
                    <div className="flex items-center gap-1">
                      <MousePointer className="h-3 w-3" />
                      <span>{url.clickCount} clicks</span>
                    </div>
                    <div className="flex items-center gap-1">
                      <Calendar className="h-3 w-3" />
                      <span>Created {formatDate(url.createdAt)}</span>
                    </div>
                  </div>
                </div>
                
                <div className="flex items-center gap-2 shrink-0">
                  <Button
                    onClick={() => handleCopy(url.shortUrl)}
                    variant="outline"
                    size="sm"
                    className="h-8 w-8 p-0"
                  >
                    {copiedUrl === "http://localhost:3000/" + url.shortUrl ? (
                      <Check className="h-3 w-3 text-green-600" />
                    ) : (
                      <Copy className="h-3 w-3" />
                    )}
                  </Button>
                  
                  <Button
                    onClick={() => handleDelete(url.shortUrl)}
                    variant="outline"
                    size="sm"
                    className="h-8 w-8 p-0 text-red-600 hover:text-red-700 hover:bg-red-50"
                  >
                    <Trash2 className="h-3 w-3" />
                  </Button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  )
} 