#!/usr/bin/env groovy

import ennva.example.Docker

def call() {
    return new Docker(this).incrementVersion()
}