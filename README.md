# Pra-frentex Banking Login

Biblioteca Java 21 para autenticação e autorização, distribuída como um pacote
Maven pelo GitHub Packages.

## Coordenadas Maven

```xml
<dependency>
    <groupId>org.pra_frentex</groupId>
    <artifactId>banking-login</artifactId>
    <version>2.0.0</version>
</dependency>
```

> Substitua a versão acima pela versão que deseja utilizar.

## Pré-requisitos para publicar

- Git configurado com acesso de escrita ao repositório;
- Java 21;
- Apache Maven 3.9 ou superior;
- uma conta com permissão de escrita no repositório;
- um Personal Access Token (classic) do GitHub com o escopo
  `write:packages`. Para baixar pacotes, basta `read:packages`.

O pacote é publicado no repositório Maven definido em `pom.xml`:

```text
https://maven.pkg.github.com/guilhermeportella/pra_frentex_Banking_login
```

## Configurar a autenticação do Maven

Crie ou atualize o arquivo `~/.m2/settings.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <servers>
        <server>
            <id>github</id>
            <username>SEU_USUARIO_GITHUB</username>
            <password>SEU_TOKEN_CLASSIC</password>
        </server>
    </servers>
</settings>
```

O valor de `<id>` deve continuar como `github`, pois precisa ser igual ao
`distributionManagement.repository.id` configurado no `pom.xml`.

Nunca adicione o token ao repositório. O arquivo `settings.xml` deve permanecer
somente na máquina do desenvolvedor. Em sistemas Unix, restrinja sua leitura:

```bash
chmod 600 ~/.m2/settings.xml
```

## Política de versões

As versões e tags seguem o versionamento semântico (`MAJOR.MINOR.PATCH`):

- `MAJOR`: alteração incompatível com versões anteriores, por exemplo `2.0.0`;
- `MINOR`: nova funcionalidade compatível, por exemplo `1.2.0`;
- `PATCH`: correção compatível, por exemplo `1.1.1`.

A versão no `pom.xml` não contém o prefixo `v`, enquanto a tag Git contém:

```text
pom.xml: 1.2.0
tag Git: v1.2.0
```

Uma versão publicada no GitHub Packages deve ser considerada imutável. Nunca
reutilize o mesmo número nem mova uma tag de release já publicada.

## Publicar uma nova versão

Os exemplos abaixo publicam a versão `1.2.0`. Troque esse número pela versão
correta antes de executar os comandos.

### 1. Atualizar a branch principal

```bash
git switch main
git pull --ff-only origin main
git status --short
```

O último comando não deve apresentar arquivos pendentes. Finalize ou guarde as
alterações locais antes de preparar a release.

### 2. Atualizar a versão do projeto

```bash
mvn versions:set -DnewVersion=1.2.0 -DgenerateBackupPoms=false
```

Confirme que apenas a mudança esperada foi aplicada:

```bash
git diff -- pom.xml
```

### 3. Compilar e executar todos os testes

```bash
mvn clean verify
```

Não publique enquanto houver falhas de compilação ou testes.

### 4. Criar o commit da release

```bash
git add pom.xml
git commit -m "chore(release): v1.2.0"
```

Se houver um changelog ou outros arquivos próprios da release, revise-os e
adicione-os explicitamente ao mesmo commit. Evite `git add .`, pois ele pode
incluir arquivos não relacionados.

### 5. Publicar o pacote no GitHub Packages

```bash
mvn deploy
```

O Maven executará novamente o ciclo de build e enviará estes artefatos usando as
coordenadas definidas no `pom.xml`:

```text
org.pra_frentex:banking-login:1.2.0
```

Confirme no GitHub, na área **Packages** do repositório, que a nova versão está
disponível antes de criar a tag.

### 6. Criar e enviar a tag

Crie uma tag anotada no commit que foi efetivamente publicado:

```bash
git tag -a v1.2.0 -m "Release v1.2.0"
git show v1.2.0
```

Depois envie o commit e a tag juntos:

```bash
git push --atomic origin main v1.2.0
```

O modo `--atomic` evita que apenas o commit ou apenas a tag seja aceito caso uma
das referências seja rejeitada pelo servidor.

### 7. Criar a GitHub Release

Com o GitHub CLI autenticado, gere a release e suas notas a partir da tag:

```bash
gh release create v1.2.0 \
  --title "v1.2.0" \
  --generate-notes \
  --verify-tag
```

Também é possível fazer isso pela interface do GitHub em **Releases > Draft a
new release**, escolhendo a tag que acabou de ser enviada.

## Checklist de release

- [ ] A branch `main` está atualizada e sem alterações pendentes.
- [ ] A versão do `pom.xml` segue SemVer e ainda não existe no Packages.
- [ ] `mvn clean verify` terminou com sucesso.
- [ ] O commit contém somente os arquivos da release.
- [ ] `mvn deploy` terminou com sucesso.
- [ ] O pacote aparece no GitHub Packages.
- [ ] A tag anotada aponta para o commit publicado.
- [ ] O commit e a tag foram enviados ao GitHub.
- [ ] A GitHub Release foi criada com notas da versão.

## Se alguma etapa falhar

- **Falha antes do `mvn deploy`:** corrija o problema, ajuste o commit se
  necessário e execute novamente os testes.
- **Falha no `mvn deploy`:** não crie a tag. Confira o token, suas permissões e o
  `<id>github</id>` do `settings.xml`, depois tente novamente.
- **Pacote publicado, mas o push falhou:** não altere a versão nem recrie o
  pacote. Corrija o acesso ao Git e tente novamente
  `git push --atomic origin main v1.2.0`.
- **Tag criada localmente no commit errado e ainda não enviada:** remova somente
  a tag local com `git tag -d v1.2.0` e recrie-a no commit correto.
- **Tag ou pacote incorreto já publicado:** não force a movimentação da tag e
  não tente sobrescrever o pacote. Prepare uma nova versão `PATCH` e documente a
  correção nas notas da release.

## Referências

- [GitHub Packages: registro Apache Maven](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)
- [GitHub: gerenciamento de releases](https://docs.github.com/en/repositories/releasing-projects-on-github/managing-releases-in-a-repository)
- [Versionamento Semântico](https://semver.org/lang/pt-BR/)
