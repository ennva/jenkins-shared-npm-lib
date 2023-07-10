#!/usr/bin/env groovy

import ennva.example.Docker

def call(String host) {
    return new Docker(this).dockerLogin(host)
}