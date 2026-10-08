# 🔒 Security Policy

## Supported Versions

We release patches and updates for security vulnerabilities for the following versions:

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |

## Reporting a Vulnerability

If you discover a security vulnerability in **Tuneify**, please report it responsibly:

1. **Do NOT** open a public issue on GitHub.
2. Contact the lead developer directly via GitHub Security Advisories or email.
3. Include the following information:
   - Description of the vulnerability
   - Steps to reproduce
   - Potential impact
   - Any suggested remediations

## Sensitive Information

The following files contain sensitive information and must never be committed to version control:

- `google-services.json` — Firebase configuration
- `local.properties` — Local SDK configuration
- `*.keystore` / `*.jks` — App release signing keys
- `secrets.properties` — Private API keys

---

**Lead Developer**: Vivek ([@Vivek](https://github.com/Vivek))
