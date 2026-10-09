from flask import Flask, request, jsonify
import joblib

app = Flask(__name__)

model = joblib.load("scam_model.pkl")
vectorizer = joblib.load("vectorizer.pkl")


@app.route("/predict", methods=["POST"])
def predict():

    data = request.get_json()
    message = data.get("message", "")

    if not message:
        return jsonify({"error": "No message provided"}), 400

    features = vectorizer.transform([message])
    prediction = model.predict(features)[0]

    probability = model.predict_proba(features)[0][1]

    if prediction == 1:
        category = "High Risk"
    else:
        category = "Safe"

    return jsonify({
        "prediction": int(prediction),
        "category": category,
        "riskScore": round(float(probability) * 100, 2)
    })


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)