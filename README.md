# decathlon-scoring-api

MVP:

Input: name, event, result, date (for the form)
Output: name, event, result, points, date (shows in results table)
The 10 decathlon events are a fixed list (dropdown)
Results table + an "add result" form
Point calculation follows actual IAAF/World Athletics formulas
Unit tests for the point calculation logic (all 10 events + edge cases)
Data persists in PostgreSQL
Everything runs via docker-compose up
README with setup instructions + architecture decisions + dev log

