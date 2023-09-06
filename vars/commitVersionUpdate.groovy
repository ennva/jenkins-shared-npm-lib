#!/usr/bin/env groovy

import ennva.example.Docker

def call(String gitRepository, String branch) {
    return new Docker(this).commitVersionUpdate(gitRepository, branch)
}