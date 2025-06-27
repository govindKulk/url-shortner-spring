# URL Shortener Frontend

A modern, responsive frontend application for the URL Shortener service built with Next.js, TypeScript, and Tailwind CSS.

## Features

- 🔐 **Authentication**: User registration and login with JWT tokens
- 🔗 **URL Shortening**: Create short URLs from long URLs
- 📊 **Dashboard**: View and manage your shortened URLs
- 📈 **Analytics**: Track click counts for each URL
- 🎨 **Modern UI**: Clean, responsive design with Tailwind CSS
- ✅ **Form Validation**: Zod schema validation for all forms
- 🔔 **Notifications**: Toast notifications for user feedback
- 📱 **Responsive**: Works on desktop and mobile devices

## Tech Stack

- **Framework**: Next.js 15 with App Router
- **Language**: TypeScript
- **Styling**: Tailwind CSS
- **UI Components**: Custom components with shadcn/ui design system
- **Form Handling**: React Hook Form with Zod validation
- **Notifications**: Sonner toast notifications
- **Icons**: Lucide React
- **State Management**: React hooks

## Getting Started

### Prerequisites

- Node.js 18+ 
- npm or yarn
- Backend API running (Spring Boot microservices)

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd url-shortener-frontend
```

2. Install dependencies:
```bash
npm install
```

3. Set up environment variables:
Create a `.env.local` file in the root directory:
```env
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
```

4. Run the development server:
```bash
npm run dev
```

5. Open [http://localhost:3000](http://localhost:3000) in your browser.

## Project Structure

```
src/
├── app/                    # Next.js app router
│   ├── globals.css        # Global styles
│   ├── layout.tsx         # Root layout
│   └── page.tsx           # Main page
├── components/            # React components
│   ├── auth/             # Authentication components
│   │   ├── LoginForm.tsx
│   │   └── RegisterForm.tsx
│   ├── url/              # URL management components
│   │   ├── CreateUrlForm.tsx
│   │   └── UrlList.tsx
│   └── ui/               # Reusable UI components
│       ├── button.tsx
│       ├── card.tsx
│       └── input.tsx
├── lib/                  # Utility functions
│   ├── api.ts           # API service functions
│   ├── utils.ts         # General utilities
│   └── validations.ts   # Zod validation schemas
└── types/               # TypeScript type definitions
    └── index.ts
```

## API Integration

The frontend integrates with the Spring Boot microservices:

- **Authentication**: `/api/auth/*` endpoints
- **URL Management**: `/api/urls/*` endpoints
- **JWT Token**: Stored in localStorage for authentication
- **User ID**: Sent in `X-User-ID` header for URL operations

## Key Features

### Authentication
- User registration and login
- JWT token management
- Automatic token validation
- Secure logout

### URL Management
- Create short URLs with validation
- View all user's URLs
- Delete URLs
- Copy URLs to clipboard
- View click statistics

### User Experience
- Responsive design
- Loading states
- Error handling
- Success notifications
- Form validation

## Development

### Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run start` - Start production server
- `npm run lint` - Run ESLint

### Code Style

- TypeScript for type safety
- ESLint for code quality
- Prettier for code formatting
- Tailwind CSS for styling

## Deployment

The application can be deployed to Vercel, Netlify, or any other hosting platform that supports Next.js.

1. Build the application:
```bash
npm run build
```

2. Deploy the `out` directory or use the platform's deployment tools.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is part of the URL Shortener internship project.
