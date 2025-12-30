<?php
if (!isset($conn)) {
    require_once 'backend/dbconnect.php';
}

$contact_info = null;
$result = $conn->query("SELECT * FROM contact_info ORDER BY id ASC LIMIT 1");
if ($result && $result->num_rows > 0) {
    $contact_info = $result->fetch_assoc();
}
?>

<section class="contact-section py-5 bg-light"> <div class="container">
        <h1 class="mb-5 text-center text-dark fw-bold">Hubungi Kami</h1> <?php if ($contact_info): ?>
            <div class="row justify-content-center g-4">
                <div class="col-lg-6 col-md-10">
                    <div class="card h-100 shadow-lg border-0 contact-info-card"> <div class="card-body p-5">
                            <h4 class="card-title mb-4 text-primary fw-bold">Informasi Kontak Kami</h4>
                            <ul class="list-unstyled contact-list">
                                <?php if ($contact_info['address']): ?>
                                    <li class="d-flex align-items-start mb-3">
                                        <i class="bi bi-geo-alt-fill me-3 fs-4 text-secondary"></i>
                                        <div>
                                            <p class="fw-bold mb-0">Alamat:</p>
                                            <p class="text-muted mb-0"><?= nl2br(htmlspecialchars($contact_info['address'])) ?></p>
                                        </div>
                                    </li>
                                <?php endif; ?>
                                <?php if ($contact_info['phone_number']): ?>
                                    <li class="d-flex align-items-start mb-3">
                                        <i class="bi bi-telephone-fill me-3 fs-4 text-secondary"></i>
                                        <div>
                                            <p class="fw-bold mb-0">Telepon:</p>
                                            <p class="text-muted mb-0"><a href="tel:<?= htmlspecialchars($contact_info['phone_number']) ?>" class="text-decoration-none text-muted hover-primary"><?= htmlspecialchars($contact_info['phone_number']) ?></a></p>
                                        </div>
                                    </li>
                                <?php endif; ?>
                                <?php if ($contact_info['email']): ?>
                                    <li class="d-flex align-items-start mb-3">
                                        <i class="bi bi-envelope-fill me-3 fs-4 text-secondary"></i>
                                        <div>
                                            <p class="fw-bold mb-0">Email:</p>
                                            <p class="text-muted mb-0"><a href="mailto:<?= htmlspecialchars($contact_info['email']) ?>" class="text-decoration-none text-muted hover-primary"><?= htmlspecialchars($contact_info['email']) ?></a></p>
                                        </div>
                                    </li>
                                <?php endif; ?>
                                <?php if ($contact_info['whatsapp_number']): ?>
                                    <li class="d-flex align-items-start mb-3">
                                        <i class="bi bi-whatsapp me-3 fs-4 text-secondary"></i>
                                        <div>
                                            <p class="fw-bold mb-0">WhatsApp:</p>
                                            <p class="text-muted mb-0"><a href="https://wa.me/<?= htmlspecialchars(str_replace([' ', '-', '+'], '', $contact_info['whatsapp_number'])) ?>" target="_blank" class="text-decoration-none text-muted hover-primary"><?= htmlspecialchars($contact_info['whatsapp_number']) ?></a></p>
                                        </div>
                                    </li>
                                <?php endif; ?>
                            </ul>

                            <?php if ($contact_info['facebook_url'] || $contact_info['instagram_url'] || $contact_info['twitter_url']): ?>
                                <h5 class="mt-5 mb-3 text-primary fw-bold">Ikuti Kami di Media Sosial</h5>
                                <div class="d-flex gap-4 social-icons">
                                    <?php if ($contact_info['facebook_url']): ?>
                                        <a href="<?= htmlspecialchars($contact_info['facebook_url']) ?>" target="_blank" class="text-decoration-none text-dark social-icon facebook"><i class="bi bi-facebook fs-2"></i></a>
                                    <?php endif; ?>
                                    <?php if ($contact_info['instagram_url']): ?>
                                        <a href="<?= htmlspecialchars($contact_info['instagram_url']) ?>" target="_blank" class="text-decoration-none text-dark social-icon instagram"><i class="bi bi-instagram fs-2"></i></a>
                                    <?php endif; ?>
                                    <?php if ($contact_info['twitter_url']): ?>
                                        <a href="<?= htmlspecialchars($contact_info['twitter_url']) ?>" target="_blank" class="text-decoration-none text-dark social-icon twitter-x"><i class="bi bi-twitter-x fs-2"></i></a>
                                    <?php endif; ?>
                                </div>
                            <?php endif; ?>
                        </div>
                    </div>
                </div>

                <?php if ($contact_info['Maps_embed']): ?>
                    <div class="col-lg-6 col-md-10">
                        <div class="card h-100 shadow-lg border-0 map-card">
                            <div class="card-body p-0 d-flex flex-column">
                                <h4 class="card-title px-4 pt-4 mb-3 text-primary fw-bold">Lokasi Kami</h4>
                                <div class="ratio ratio-16x9 flex-grow-1 map-embed">
                                    <?= $contact_info['Maps_embed'] ?>
                                </div>
                            </div>
                        </div>
                    </div>
                <?php endif; ?>
            </div>
        <?php else: ?>
            <div class="alert alert-info text-center mt-5" role="alert">
                Informasi kontak belum diatur. Silakan hubungi administrator.
            </div>
        <?php endif; ?>
    </div>
</section>

<style>
    .contact-section {
        background: linear-gradient(to bottom, #f8f9fa, #e9ecef); 
    }

    .contact-info-card, .map-card {
        border-radius: 1rem; 
        transition: transform 0.3s ease-in-out, box-shadow 0.3s ease-in-out;
        overflow: hidden; 
    }
    .contact-info-card:hover, .map-card:hover {
        transform: translateY(-5px); 
        box-shadow: 0 1rem 3rem rgba(0, 0, 0, 0.175) !important; 
    }

    .contact-list li {
        align-items: center; 
    }
    .contact-list i {
        font-size: 1.8rem; 
        color: #007bff; 
        min-width: 40px; 
        text-align: center;
    }
    .contact-list p {
        font-size: 1.05rem; 
    }
    .contact-list a.hover-primary:hover {
        color: #007bff !important; 
    }

    .social-icons a {
        display: inline-flex; 
        align-items: center;
        justify-content: center;
        width: 50px; 
        height: 50px;
        border-radius: 50%; 
        background-color: #f0f2f5; 
        color: #495057; 
        transition: background-color 0.3s ease, color 0.3s ease, transform 0.2s ease;
    }
    .social-icons a:hover {
        transform: translateY(-3px); 
    }
    .social-icons a.facebook:hover {
        background-color: #1877F2;
        color: white;
    }
    .social-icons a.instagram:hover {
        background: linear-gradient(45deg, #f09433 0%,#e6683c 25%,#dc2743 50%,#cc2366 75%,#bc1888 100%);
        color: white;
    }
    .social-icons a.twitter-x:hover {
        background-color:rgb(14, 18, 24); 
        color: white;
    }

    .map-embed iframe {
        border: 0;
        border-radius: 0 0 1rem 1rem; 
    }
</style>