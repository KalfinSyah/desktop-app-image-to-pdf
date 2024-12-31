from tkinter import filedialog
from PIL import Image, ImageTk

def content(root, tk):

    def select_image():
        global file_path
        file_path = filedialog.askopenfilename(filetypes=[("Image Files", "*.jpg;*.jpeg;*.png;*")])
        if file_path:
            img = Image.open(file_path)
            img = img.resize((200, 200)) 
            img_tk = ImageTk.PhotoImage(img)
            info_label.config(text=f"selected image: \n\n{file_path}")
            image_label.config(image=img_tk)
            image_label.image = img_tk
            convert_button.config(state="normal")

    def convert_to_pdf():
        if file_path:
            img = Image.open(file_path)
            img.convert("RGB")
            pdf_path = file_path.replace(".jpg", ".pdf").replace(".jpeg", ".pdf").replace(".png", ".pdf")
            img.save(pdf_path, "PDF")
            done_label.config(text=f"saved at: \n\n{pdf_path}")

    select_button = tk.Button(root, text="Select an Image", command=select_image)
    select_button.pack(pady=10, padx=10)

    info_label = tk.Label(root, text="no image selected")
    info_label.pack(pady=10, padx=10)

    img = Image.open("./preview.png")
    img_tk = ImageTk.PhotoImage(img)
    image_label = tk.Label(root, text="no preview yet")
    image_label.config(image=img_tk)
    image_label.image = img_tk
    image_label.pack(pady=10, padx=10)

    convert_button = tk.Button(root, text="Convert to PDF", command=convert_to_pdf)
    convert_button.config(state="disabled")
    convert_button.pack(pady=10, padx=10)

    done_label = tk.Label(root, text="")
    done_label.pack(pady=10, padx=10)