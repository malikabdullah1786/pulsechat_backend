# 🚢 PulseChat Backend: Step-by-Step Render Deployment Guide

This guide will walk you through deploying your Java Spring Boot backend to Render.com using the Docker configuration I've already pushed to your repository.

---

### **Step 1: Connect your GitHub Account**
1. Go to [dashboard.render.com](https://dashboard.render.com/) and log in.
2. If you haven't already, connect your GitHub account to Render.

### **Step 2: Create a New Web Service**
1. Click the **"New+"** button in the top right corner.
2. Select **"Web Service"**.
3. Under "Connect a repository", find and select **`pulsechat_backend`**.

### **Step 3: Configure Service Details**
Set the following options on the configuration page:
- **Name**: `pulsechat-backend`
- **Region**: Choose the one closest to you (e.g., Singapore or US East).
- **Branch**: `main`
- **Runtime**: Select **`Docker`** (This is very important! Render will use the `Dockerfile` I created).
- **Instance Type**: Select the **"Free"** tier.

### **Step 4: Add Environment Variables**
Scroll down to the **"Environment Variables"** section and click **"Add Environment Variable"**. Add these two:

| Key | Value | Where to find it? |
| :--- | :--- | :--- |
| **`SUPABASE_URL`** | `https://fxrmlnqgrlbzjaoejceq.supabase.co` | From your Supabase project settings. |
| **`SUPABASE_SERVICE_KEY`** | *[Your Secret Service Role Key]* | **Supabase Dashboard** -> Settings -> API -> `service_role` (secret) key. |

> [!WARNING]
> Do **NOT** use the `anon`/`public` key for `SUPABASE_SERVICE_KEY`. Use the **Service Role** key so the backend can bypass row-level security to manage users.

### **Step 5: Deploy**
1. Click **"Create Web Service"** at the bottom of the page.
2. Render will start building your Docker image. This usually takes 3–5 minutes because it needs to download Maven dependencies and compile your Java code.

---

### **Step 6: Verify the Deployment**
1. Once the build is finished, the status will change to **"Live"**.
2. You will see a URL at the top (starts with `https://pulsechat-backend...`).
3. Open that URL. If you see a Whitelabel Error Page (404), it's actually **GOOD news**—it means the server is running, but there just isn't a "Home" page set up.

---

### **Final Step: Update your App**
Once the URL is live, we need to update your mobile app's `.env` file:
1. Go to your local **`pulsechat/.env`** file.
2. Change `EXPO_PUBLIC_BACKEND_URL` to your new Render URL.
   ```bash
   # Example:
   EXPO_PUBLIC_BACKEND_URL=https://pulsechat-backend-xxxx.onrender.com
   ```
3. Restart your app: `npx expo start --clear`.

---

**Need help with any of these steps? Just let me know where you are stuck!** 🚀🤝✨
