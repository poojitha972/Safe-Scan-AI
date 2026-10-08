from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
import joblib

messages = [
    "Your account has been blocked. Verify your account immediately.",
    "Congratulations you won a cash prize. Click the link to claim.",
    "Your bank account will be suspended. Verify your OTP now.",
    "Urgent! Your package is waiting. Pay the delivery fee.",
    "You have won a lottery. Send your bank details to receive the prize.",
    "Click this link to get your free reward.",
    "Your account has suspicious activity. Login immediately.",
    "You are selected for a reward. Claim it now.",
    "Your refund is ready. Provide your bank details.",
    "Your loan has been approved. Pay the processing fee now.",
    "Meeting is scheduled for tomorrow at 10 AM.",
    "Please submit your assignment before Friday.",
    "Can we meet in the library today?",
    "Your class starts at 9 AM tomorrow.",
    "Don't forget to bring your project file.",
    "Happy birthday! Have a great day.",
    "The college has announced a holiday tomorrow.",
    "Please send me the notes when you are free.",
    "Let's meet after class.",
    "Your internship application has been received."
]

labels = [
    1, 1, 1, 1, 1,
    1, 1, 1, 1, 1,
    0, 0, 0, 0, 0,
    0, 0, 0, 0, 0
]

vectorizer = TfidfVectorizer()
X = vectorizer.fit_transform(messages)

model = LogisticRegression()
model.fit(X, labels)

joblib.dump(model, "scam_model.pkl")
joblib.dump(vectorizer, "vectorizer.pkl")

print("ML model trained successfully!")