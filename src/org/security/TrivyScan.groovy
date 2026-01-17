package org.security

class TrivyScan {
    static void scan(def steps, String image) {
        steps.sh """
          trivy image \
          --config trivy.yaml \
          ${image}
        """
    }
}
