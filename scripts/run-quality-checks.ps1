param(
    [Parameter(Mandatory = $true)]
    [ValidateSet("pre-commit")]
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

Invoke-Step -Title "Frontend format check" -Command "npm run format:check" -Workdir $frontend
Invoke-Step -Title "Frontend lint" -Command "npm run lint" -Workdir $frontend
Invoke-Step -Title "Backend format check" -Command "mvn spotless:check" -Workdir $backend
Invoke-Step -Title "Backend Checkstyle" -Command "mvn checkstyle:check" -Workdir $backend
Invoke-Step -Title "Backend Fast Tests (Arch & Swagger)" -Command "mvn test -Dtest='CleanArchitectureTest,SwaggerGeneratorTest'" -Workdir $backend
