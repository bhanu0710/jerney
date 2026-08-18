require('dotenv').config();
const express = require('express');
const cors = require('cors');
const postRoutes = require('./routes/posts');
const commentRoutes = require('./routes/comments');
const db = require('./db');
const {
    client,
    httpRequests,
    httpDuration
} = require("./metrics");

const app = express();
const PORT = process.env.PORT || 5000;

// Middleware
app.use(cors());
app.use(express.json());

app.use((req,res,next)=>{

    const end = httpDuration.startTimer();

    res.on("finish",()=>{

        httpRequests.inc({
            method:req.method,
            route:req.path,
            status:res.statusCode
        });

        end({
            method:req.method,
            route:req.path,
            status:res.statusCode
        });

    });

    next();

});

// Health check
app.get('/api/health', (req, res) => {
  res.json({ status: 'ok', message: 'Jerney API is vibing ✨' });
});



// Routes
app.use('/api/posts', postRoutes);
app.use('/api/comments', commentRoutes);

// metrics endpoint 
app.get('/metrics', async (req, res) => {
  res.set('Content-Type', client.register.contentType);
  res.end(await client.register.metrics());
});

// Initialize database and start server
async function start() {
  try {
    await db.initDB();
    app.listen(PORT, '0.0.0.0', () => {
      console.log(`🚀 Jerney backend running on port ${PORT}`);
    });
  } catch (err) {
    console.error('Failed to start server:', err);
    process.exit(1);
  }
}

start();
