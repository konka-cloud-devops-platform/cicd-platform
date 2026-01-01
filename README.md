# CI/CD Platform

This repository contains centralized CI/CD building blocks used across multiple projects.

## What this repo includes
- Jenkins Shared Libraries for standardized pipelines
- GitHub Actions reusable workflows for modern CI/CD
- Terraform, Docker, and Kubernetes deployment patterns

## Why this approach
- Avoids pipeline duplication
- Enforces best practices
- Makes onboarding new projects faster
- Provides auditability and consistency

## How it is used
- Jenkins pipelines import shared libraries
- GitHub repositories call reusable workflows using `workflow_call`
