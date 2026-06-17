param(
  [Parameter(Mandatory = $true)]
  [ValidateSet("OK", "SLOW", "ERROR", "FLAKY")]
  [string]$Mode,

  [int]$SlowDelayMs = 7000,
  [int]$ErrorStatus = 500,
  [string]$BaseUrl = "http://localhost:8086"
)

$payload = @{
  mode = $Mode
}

if ($Mode -eq "SLOW") {
  $payload.slowDelayMs = $SlowDelayMs
}

if ($Mode -eq "ERROR" -or $Mode -eq "FLAKY") {
  $payload.errorStatus = $ErrorStatus
}

$json = $payload | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri "$BaseUrl/api/demo/control/mode" `
  -ContentType "application/json" `
  -Body $json
