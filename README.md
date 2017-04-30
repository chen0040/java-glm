# java-glm
Package implements the generalized linear model in Java

[![Build Status](https://travis-ci.org/chen0040/java-glm.svg?branch=master)](https://travis-ci.org/chen0040/java-glm) [![Coverage Status](https://coveralls.io/repos/github/chen0040/java-glm/badge.svg?branch=master)](https://coveralls.io/github/chen0040/java-glm?branch=master) 

# Introduction

GLM is generalized linear model for exponential family of distribution model b = g(a).
g(a) is the inverse link function.

Therefore, for a regressions characterized by inverse link function g(a), the regressions problem be formulated
as we are looking for model coefficient set x in/**/
g(A * x) = b + e
And the objective is to find x such for the following objective:
min (g(A * x) - b).transpose * W * (g(A * x) - b)

Suppose we assumes that e consist of uncorrelated naive variables with identical variance, then W = sigma^(-2) * I,
and The objective min (g(A * x) - b) * W * (g(A * x) - b).transpose is reduced to the OLS form:
min || g(A * x) - b ||^2

# Iteratively Re-weighted Least Squares estimation (IRLS)

In regressions, we tried to find a set of model coefficient such for
A * x = b + e

A * x is known as the model matrix, b as the response vector, e is the error terms.

In OLS (Ordinary Least Square), we assumes that the variance-covariance matrix V(e) = sigma^2 * W, where:
  W is a symmetric positive definite matrix, and is a diagonal matrix
  sigma is the standard error of e

In OLS (Ordinary Least Square), the objective is to find x_bar such that e.transpose * W * e is minimized (Note that since W is positive definite, e * W * e is alway positive)
In other words, we are looking for x_bar such as (A * x_bar - b).transpose * W * (A * x_bar - b) is minimized

Let y = (A * x - b).transpose * W * (A * x - b)
Now differentiating y with respect to x, we have
dy / dx = A.transpose * W * (A * x - b) * 2

To find min y, set dy / dx = 0 at x = x_bar, we have
A.transpose * W * (A * x_bar - b) = 0

Transform this, we have
A.transpose * W * A * x_bar = A.transpose * W * b

Multiply both side by (A.transpose * W * A).inverse, we have
x_bar = (A.transpose * W * A).inverse * A.transpose * W * b
This is commonly solved using IRLS
The implementation of Glm based on iteratively re-weighted least squares estimation (IRLS)

