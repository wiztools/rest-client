## SSL & TLS

TLS is essentially an updated and improved version of SSL. The terms "SSL" and "TLS" are often used interchangeably. RESTClient, until recent versions, used the terminology "SSL", although it has long supported TLS.

## Trust Store & Key Store

A key store is used to store your own private keys and certificates, which prove your identity. It contains:

* Your private keys
* Your digital certificates containing your public keys
* Optionally, certificates from others you communicate with

A trust store, on the other hand, is used to store certificates from others that you trust, similar to how your web browser stores certificates of trusted websites. It contains:

* Public certificates of trusted entities (Certificate Authorities or specific servers)
* No private keys
* Used to verify the authenticity of incoming connections

Here's a practical example:

* When your Java application acts as a server: It uses the key store to present its credentials to clients
* When your application acts as a client: It uses the trust store to verify the identity of servers it connects to

Key properties that distinguish them:

1. Key store requires a password to protect private keys, while trust store typically doesn't need the same level of security since it only contains public certificates
2. Key store is specific to your application/server identity, while trust store defines which external entities you trust
3. Key store is used for outgoing authentication, while trust store is used for incoming authentication

## Mutual TLS or mTLS

Mutual TLS (also called Two-way SSL) is a security protocol where both client and server verify each other's identity using digital certificates. Unlike one-way SSL where only the server authenticates itself, in two-way SSL:

1. Server presents its certificate to the client
2. Client verifies server's certificate
3. Client presents its certificate to the server
4. Server verifies client's certificate

This ensures both parties are legitimate and creates an encrypted channel for data transmission. It's commonly used in B2B applications, APIs, and systems requiring high security.

Key requirements:

* Both client and server need valid SSL certificates
* Server must be configured to request client certificates
* Client must have its certificate and private key installed

Primary advantage is stronger security. Main drawback is increased complexity in certificate management and deployment.

RESTClient treats requests with both *Truststore* and *Keystore* configurations as mTLS.

## Self-signed Certificate

A self-signed certificate is a digital certificate that's signed by the same entity whose identity it certifies, rather than by a trusted Certificate Authority (CA). It contains the same information as a regular SSL/TLS certificate but lacks third-party validation.

Key aspects:

1. Created and signed by the same person or organization that owns the certificate
2. Free to generate and useful for development/testing environments
3. Browsers display security warnings since there's no trusted third-party verification
4. Not suitable for production websites as users will see security warnings
5. Commonly used in internal networks, development environments, and testing scenarios

When using self-signed certificates in RESTClient, remember to *Ignore cert errors* for the request to work.

## Hostname Verification

Hostname verification is a crucial security check in SSL/TLS that ensures the hostname (domain name) the client is trying to connect to matches the hostname specified in the server's certificate. This helps prevent man-in-the-middle attacks and server impersonation.

Here's how it works:

1. When a client initiates an SSL/TLS connection to a server (e.g., visiting https://example.com), the server presents its certificate.
2. The certificate contains a field called Subject Alternative Name (SAN) and/or Common Name (CN) that lists the domain names the certificate is valid for.
3. The client checks if the hostname it's connecting to matches any of the names listed in the certificate's SAN or CN fields. The match must be exact, though wildcards are sometimes permitted (e.g., *.example.com would match sub.example.com).

Without hostname verification, an attacker could:

1. Obtain a valid certificate for their own domain (attacker.com)
2. Intercept traffic meant for the target domain (example.com)
3. Present their valid certificate to the client

The connection would still be encrypted, but the client would be talking to the wrong server. Hostname verification prevents this by ensuring the certificate is actually for the intended destination.

RESTClient has option to *Disable hostname verification*.
