#!/usr/bin/env groovy

import ennva.example.Docker

def call(String host, String imageName) {
    return new Docker(this).dockerPush(host, imageName)
}