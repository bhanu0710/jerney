const client = require("prom-client");

client.collectDefaultMetrics();

const httpRequests = new client.Counter({
  name: "http_requests_total",
  help: "Total HTTP Requests",
  labelNames: ["method", "route", "status"]
});

const httpDuration = new client.Histogram({
  name: "http_request_duration_seconds",
  help: "HTTP Request Duration",
  labelNames: ["method", "route", "status"],
  buckets: [0.1,0.2,0.5,1,2,5]
});

module.exports = {
  client,
  httpRequests,
  httpDuration
};
