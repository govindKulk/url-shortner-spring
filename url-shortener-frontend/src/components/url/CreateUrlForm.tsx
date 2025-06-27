"use client"

import { useState } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { createUrlSchema } from "@/lib/validations"
import { type CreateUrlRequest } from "@/types"
import { apiService } from "@/lib/api"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { toast } from "sonner"
import { Loader2, Link, Copy, Check } from "lucide-react"
import { copyToClipboard } from "@/lib/utils"

interface CreateUrlFormProps {
  onUrlCreated: () => void
}

export function CreateUrlForm({ onUrlCreated }: CreateUrlFormProps) {
  const [isLoading, setIsLoading] = useState(false)
  const [createdUrl, setCreatedUrl] = useState<string | null>(null)
  const [copied, setCopied] = useState(false)

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<CreateUrlRequest>({
    resolver: zodResolver(createUrlSchema),
  })

  const onSubmit = async (data: CreateUrlRequest) => {
    setIsLoading(true)
    try {
      const response = await apiService.createShortUrl(data)
      
      if (response.success) {
        setCreatedUrl(response.shortUrl)
        toast.success("URL shortened successfully!")
        reset()
        onUrlCreated()
      } else {
        toast.error(response.message || "Failed to create short URL")
      }
    } catch (error) {
      toast.error("Failed to create short URL. Please try again.")
    } finally {
      setIsLoading(false)
    }
  }

  const handleCopy = async () => {
    if (createdUrl) {
      await copyToClipboard(createdUrl)
      setCopied(true)
      toast.success("URL copied to clipboard!")
      setTimeout(() => setCopied(false), 2000)
    }
  }

  return (
    <Card className="w-full">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Link className="h-5 w-5" />
          Create Short URL
        </CardTitle>
        <CardDescription>
          Enter a long URL to create a short, shareable link
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div className="space-y-2">
            <Input
              {...register("originalUrl")}
              placeholder="https://example.com/very-long-url-that-needs-shortening"
              disabled={isLoading}
            />
            {errors.originalUrl && (
              <p className="text-sm text-red-500">{errors.originalUrl.message}</p>
            )}
          </div>

          <Button type="submit" className="w-full" disabled={isLoading}>
            {isLoading ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Creating...
              </>
            ) : (
              "Create Short URL"
            )}
          </Button>
        </form>

        {createdUrl && (
          <div className="mt-6 p-4 bg-green-50 border border-green-200 rounded-lg">
            <p className="text-sm font-medium text-green-800 mb-2">Your short URL:</p>
            <div className="flex items-center gap-2">
              <Input
                value={createdUrl}
                readOnly
                className="bg-white"
              />
              <Button
                onClick={handleCopy}
                variant="outline"
                size="icon"
                className="shrink-0"
              >
                {copied ? (
                  <Check className="h-4 w-4 text-green-600" />
                ) : (
                  <Copy className="h-4 w-4" />
                )}
              </Button>
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  )
} 