// package org.security

// class TrivyScan {
//   static void scan(String image) {
//     sh "trivy image --config trivy.yaml ${image}"
//   }
// }
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
