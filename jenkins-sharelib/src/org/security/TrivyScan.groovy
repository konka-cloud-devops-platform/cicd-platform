package org.security

class TrivyScan {
  static void scan(String image) {
    sh "trivy image --config trivy.yaml ${image}"
  }
}
