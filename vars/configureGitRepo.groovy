#!/usr/bin/env groovy

import ennva.example.Docker

def call(String gitRepository, String branch) {
    return new Docker(this).configureGitRepo(gitRepository, branch)
}