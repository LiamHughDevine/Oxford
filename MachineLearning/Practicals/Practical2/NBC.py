import numpy as np
from scipy.stats import bernoulli, norm

class NBC:
    def __init__(self, feature_types, num_classes):
        self.feature_types = feature_types
        self.num_features = len(self.feature_types)
        self.num_classes = num_classes
        self.pi = np.zeros(num_classes)
        self.theta = np.empty((self.num_features, self.num_classes), dtype=object)

    def fit(self, Xtrain, ytrain):
        for c in range(self.num_classes):
            mean_seen = np.mean(ytrain == c)
            if mean_seen == 0:
                self.pi[c] = float("-inf")
                continue

            self.pi[c] = np.log(np.mean(ytrain == c))
            for j in range(self.num_features):
                filtered = np.extract(ytrain == c, Xtrain[:, j])
                if self.feature_types[j] == "r":
                    mean = np.mean(filtered)
                    std = np.std(filtered).item()
                    std = max(std, 1e-6)
                    self.theta[j, c] = norm(loc=mean, scale=std)
                elif self.feature_types[j] == "b":
                    total = np.sum(filtered) + 1
                    mean = total / (filtered.shape[0] + 2)
                    self.theta[j, c] = bernoulli(mean)

    def log_prob(self, x, c):
        log_prob = self.pi[c].item()
        if log_prob == float("-inf"):
            return log_prob
        for j in range(self.num_features):
            if self.feature_types[j] == "r":
                pdf = self.theta[j, c].pdf(x[j])
                if pdf > 0:
                    log_prob += np.log(pdf)
                else:
                    log_prob += float("-inf")
            elif self.feature_types[j] == "b":
                pmf = self.theta[j, c].pmf(x[j])
                if pmf > 0:
                    log_prob += np.log(pmf)
                else:
                    log_prob += float("-inf")
        return log_prob
    
    def predict_element(self, x):
        predictions = np.array([self.log_prob(x, c) for c in range(self.num_classes)])
        return np.argmax(predictions)


    def predict(self, Xtest):
        return np.apply_along_axis(self.predict_element, 1, Xtest)
