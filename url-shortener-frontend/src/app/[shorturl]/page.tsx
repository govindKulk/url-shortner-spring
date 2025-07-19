import { redirect } from "next/navigation";

export default async function Page({ params }: { params: Promise<{ shorturl: string }> }) {

  const {shorturl} = await params;
  // Call the backend directly from the server
  const res = await fetch(`http://localhost:8080/api/urls/${shorturl}`, {
    // Don't follow redirects automatically
    redirect: "manual",
  });

  // If backend returns a 302, get the Location header and redirect
  if (res.status === 302) {
    const location = res.headers.get("location");
    if (location) {
      redirect(location);
    }
  }

  // If not found or any other error, show fallback UI
  return (
    <div className="flex flex-col items-center justify-center h-screen">
      <h1 className="text-2xl font-bold">Invalid URL</h1>
      <p className="text-gray-500">
        The URL you are trying to access is invalid or has expired.
      </p>
    </div>
  );
}