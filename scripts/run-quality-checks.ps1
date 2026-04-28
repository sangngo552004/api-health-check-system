param(
    [Parameter(Mandatory = $true)]
    [ValidateSet("pre-commit", "pre-push")]
    [string]$Mode
)

$ErrorActionPreference = "Stop"

function Invoke-Step {
    param(
        [string]$Title,
        [string]$Command,
        [string]$Workdir
    )

    Write-Host ""
    Write-Host "==> $Title"
    Push-Location $Workdir
    try {
        Invoke-Expression $Command
        if ($LASTEXITCODE -ne 0) {
            throw "Command failed: $Command"
        }
    }
    finally {
        Pop-Location
    }
}

$root = Split-Path -Parent $PSScriptRoot
$backend = Join-Path $root "backend"
$frontend = Join-Path $root "frontend"

if ($Mode -eq "pre-commit") {
    Invoke-Step -Title "Frontend lint" -Command "npm run lint" -Workdir $frontend
    Invoke-Step -Title "Backend architecture and tests" -Command "mvn test" -Workdir $backend
    exit 0
}

Invoke-Step -Title "Frontend production build" -Command "npm run build" -Workdir $frontend
Invoke-Step -Title "Backend architecture and tests" -Command "mvn test" -Workdir $backend
