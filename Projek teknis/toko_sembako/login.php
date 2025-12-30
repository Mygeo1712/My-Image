<?php
session_start();
if (isset($_SESSION['user_id'])) {
    header("Location: index.php");
    exit();
}

$error = '';
if (isset($_SESSION['login_error'])) {
    $error = $_SESSION['login_error'];
    unset($_SESSION['login_error']);
}
?>

<!DOCTYPE html>
<html lang="en">    
<head>
    <meta charset="UTF-8">
    <title>Login Page</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css">

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        body {
            background-size: cover;
            background-repeat: no-repeat;
            background-position: center;
            font-family: 'Segoe UI', sans-serif;
            transition: background-image 0.5s ease-in-out;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 1rem;
        }

        .login-container {
            background-color: rgba(0, 0, 0, 0.6);
            border-radius: 25px;
            padding: 2.5rem 1.5rem;
            color: #fff;
            max-width: 300px;
            width: 100%;
            text-align: center;
            box-shadow: 0 0 10px rgba(255, 255, 255, 0.2);
            font-size: 1rem;
        }

        .form-control {
            font-size: 1rem;
            padding: 0.75rem;
        }

        .btn {
            font-size: 1rem;
            padding: 0.6rem;
        }

        .checkbox-label, .footer-note {
            font-size: 0.85rem;
        }

        .footer-note {
            color: #ccc;
            font-size: 0.75rem;
        }

        .hover-blue:hover {
            color: #0d6efd !important;
        }

        @media (max-width: 375px) {
            .login-container {
                padding: 1.5rem 1rem;
                margin: 2rem auto;
                font-size: 0.9rem;
            }
        }

        @media (min-width: 576px) {
            .login-container {
                max-width: 320px;
            }
        }

        @media (min-width: 768px) {
            .login-container {
                max-width: 340px;
                font-size: 1.05rem;
            }

            .form-control, .btn {
                font-size: 1.05rem;
            }
        }

        @media (min-width: 992px) {
            .login-container {
                max-width: 360px;
                padding: 3rem 2rem;
                font-size: 1.1rem;
            }
        }

        @media (min-width: 1200px) {
            .login-container {
                max-width: 380px;
            }
        }
    </style>
</head>

<body>
    <div class="login-container">
        <h3 class="mb-4"><strong>Login</strong></h3>

        <?php if ($error): ?>
            <div class="alert alert-danger"><?= htmlspecialchars($error) ?></div>
        <?php endif; ?>

        <form method="POST" action="backend/login_handle.php">
            <input type="text" name="username" class="form-control mb-3" placeholder="Username" required>
            <input type="password" name="password" class="form-control mb-2" placeholder="Password" id="password" required>

            <div class="form-check text-start mb-3">
                <input type="checkbox" class="form-check-input" id="showPassword" onclick="togglePassword()">
                <label for="showPassword" class="form-check-label checkbox-label">Show Password</label>
            </div>

            <button type="submit" class="btn btn-primary w-100 mb-3">Sign in</button>

            <div class="d-flex justify-content-center align-items-center small text-white text-nowrap">
                <span class="me-1">Belum punya akun?</span>
                <a href="register.php" class="text-white text-decoration-none hover-blue">Sign Up</a>
            </div>
        </form>

        <div class="mt-3 footer-note">©2025 Toko Sembako</div>
    </div>

    <script>
        function togglePassword() {
            const pwField = document.getElementById("password");
            pwField.type = pwField.type === "password" ? "text" : "password";
        }

        const backgrounds = [
            'assets/background_img/bg1.jpg',
            'assets/background_img/bg2.jpg'
        ];

        function setDynamicBackground() {
            const hour = new Date().getHours();
            const index = hour % backgrounds.length;
            document.body.style.backgroundImage = `url('${backgrounds[index]}')`;
        }

        setDynamicBackground();
        setInterval(setDynamicBackground, 3600000); 
    </script>
</body>
</html>