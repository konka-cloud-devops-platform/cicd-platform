package org.security

class SBOM {
    static void generate(def steps, String image, String format = "cyclonedx-json") {
        steps.sh """
          syft ${image} \
            -o ${format} \
            > sbom.json
        """
    }
}