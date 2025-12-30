<?php
session_start();
if (!isset($_SESSION['user_id']) || $_SESSION['role'] !== 'admin') { exit('Akses ditolak'); }

require_once '../../backend/dbconnect.php';

$upload_dir = '../../uploads/posts/';
if (!is_dir($upload_dir)) {
    mkdir($upload_dir, 0755, true);
}


$id = $_POST['id'];
$title = $_POST['title'];
$content = $_POST['content'];
$author_id = $_SESSION['user_id'];
$slug = createSlug($title); 
$image_path = '';

if (isset($_FILES['image']) && $_FILES['image']['error'] == 0) {
    $image_name = time() . '_' . basename($_FILES['image']['name']);
    $target_file = $upload_dir . $image_name;
    if (move_uploaded_file($_FILES['image']['tmp_name'], $target_file)) {
        $image_path = $image_name;
    }
}

if (empty($id)) { 
    $stmt = $conn->prepare("INSERT INTO posts (title, slug, content, image, author_id) VALUES (?, ?, ?, ?, ?)");
    $stmt->bind_param("ssssi", $title, $slug, $content, $image_path, $author_id);
} else { 
    if ($image_path) {
        
        $old_img_stmt = $conn->prepare("SELECT image FROM posts WHERE id=?");
        $old_img_stmt->bind_param("i", $id);
        $old_img_stmt->execute();
        $old_image = $old_img_stmt->get_result()->fetch_assoc()['image'];
        if ($old_image && file_exists($upload_dir . $old_image)) {
            unlink($upload_dir . $old_image);
        }
        $old_img_stmt->close();

        $stmt = $conn->prepare("UPDATE posts SET title=?, slug=?, content=?, image=?, author_id=? WHERE id=?");
        $stmt->bind_param("ssssii", $title, $slug, $content, $image_path, $author_id, $id);
    } else {
        $stmt = $conn->prepare("UPDATE posts SET title=?, slug=?, content=?, author_id=? WHERE id=?");
        $stmt->bind_param("sssii", $title, $slug, $content, $author_id, $id);
    }
}

if ($stmt->execute()) {
    header("Location: ../manage_posts.php");
} else {
    echo "Error: " . $stmt->error;
}
$stmt->close();
?>
