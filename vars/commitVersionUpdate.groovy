#!/usr/bin/env groovy

import ennva.example.Docker

def call(String gitRepository) {
    return new Docker(this).commitVersionUpdate(gitRepository)
}