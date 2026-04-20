package com.vserve.project.util;

import java.util.List;

public class SimilarityUtil {

    public static double cosineSimilarity(List<Double> v1, List<Double> v2) {

        if (v1 == null || v2 == null || v1.isEmpty() || v2.isEmpty()) {
            return 0.0;
        }

        int size = Math.min(v1.size(), v2.size());

        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < size; i++) {

            dot += v1.get(i) * v2.get(i);
            normA += Math.pow(v1.get(i), 2);
            normB += Math.pow(v2.get(i), 2);
        }

        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}