using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.InputSystem;
using TMPro;

public class PlayerController : MonoBehaviour
{
    // Speed at which the player moves
    public float speed = 0;

    // UI text component to display count of "Pickup" objects collected
    public TextMeshProUGUI countText;
    
    // UI object to display winning text
    public GameObject winTextObject;

    // Rigidbody of the player
    private Rigidbody rb;

    private int count;

    // Movement along the x and y axes
    public float movementX;
    public float movementY;

    // Start is called before the first frame update
    void Start()
    {
        // Get and store the Rigidbody component attached to the player
        rb = GetComponent<Rigidbody>();
        count = 0;

        // Update the count display
        SetCountText();

        // Initially set the win text to be inactive
        winTextObject.SetActive(false);
    }

    // This function is called when a move input is detected
    void OnMove(InputValue movementValue)
    {
        // Convert the input value into a Vector2 for movement
        Vector2 movementVector = movementValue.Get<Vector2>();

        // Store the x and y components of the movement
        movementX = movementVector.x;
        movementY = movementVector.y;
    }

    // Function to update the displayed count of "Pickup" objects collected
    void SetCountText()
    {
        // Update the count text with current count
        countText.text = "Count: " + count.ToString();
        
        // Check if count has reached or exceeded the win condition
        if (count >=12)
        {
            // Display the win text
            winTextObject.SetActive(true);
        }
    }

    // FixedUpdate is called once per fixed frame-rate frame
    void FixedUpdate()
    {
        // Create a 3D mvmt vector using the x and y inputs
        Vector3 movement = new Vector3(movementX, 0.0f, movementY);

        // Apply force to the Rigidbody to move the player
        rb.AddForce(movement * speed);
    }

    void OnTriggerEnter(Collider other)
    {
        // Check if the object the player collided with has the "Pickup" tag
        if(other.gameObject.CompareTag("Pickup"))
        {
            // Deactivate the collided object (making it disappear)
            other.gameObject.SetActive(false);
            count += 1;

            // Update count display
            SetCountText();
        }
    }
}
